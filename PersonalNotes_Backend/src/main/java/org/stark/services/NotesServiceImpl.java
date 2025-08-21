package org.stark.services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stark.configs.CryptoConfig.AesUtil;
import org.stark.dtos.*;
import org.stark.entities.Notes;
import org.stark.entities.User;
import org.stark.exceptions.AuthException;
import org.stark.exceptions.CryptoException;
import org.stark.repositories.NotesRepository;
import org.stark.utils.PdfUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotesServiceImpl implements INotesService {

    private static final Logger log = LoggerFactory.getLogger(NotesServiceImpl.class);

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private AesUtil aesUtil;

    private static final Path PDF_STORAGE = Path.of("uploads/notes");

    // ================== TEXT NOTES ==================
    @Override
    public NotesResponseDTO createNote(NotesRequestDTO dto, User user) {
        try {
            String encryptedContent = aesUtil.encrypt(dto.content());

            Notes note = new Notes();
            note.setTitle(dto.title());
            note.setContent(encryptedContent);
            note.setUser(user);

            Notes saved = notesRepository.save(note);
            return mapToDto(saved);

        } catch (Exception e) {
            throw new CryptoException("Failed to encrypt note content", e);
        }
    }

    @Override
    public NotesResponseDTO updateNote(Long noteId, NotesRequestDTO dto, User user) {
        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new AuthException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) throw new AuthException("Unauthorized access");

        try {
            note.setTitle(dto.title());
            note.setContent(aesUtil.encrypt(dto.content()));
            Notes updated = notesRepository.save(note);
            return mapToDto(updated);
        } catch (Exception e) {
            throw new CryptoException("Failed to encrypt note content", e);
        }
    }

    @Override
    public void deleteNote(Long noteId, User user) {
        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new AuthException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) throw new AuthException("Unauthorized access");
        notesRepository.delete(note);

        if (note.getPdfFilename() != null) {
            try {
                Files.deleteIfExists(PDF_STORAGE.resolve(note.getPdfFilename()));
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public List<NotesResponseDTO> getNotes(User user) {
        return notesRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotesResponseDTO> searchNotes(User user, String keyword) {
        return notesRepository.findByUserAndTitleContainingIgnoreCase(user, keyword).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private NotesResponseDTO mapToDto(Notes note) {
        try {
            return new NotesResponseDTO(
                    note.getId(),
                    note.getTitle(),
                    aesUtil.decrypt(note.getContent()),
                    note.getUser().getUsername(),
                    note.getCreatedAt(),
                    note.getUpdatedAt()
            );
        } catch (Exception e) {
            throw new CryptoException("Failed to decrypt note content", e);
        }
    }

    // ================== PDF UPLOAD/DOWNLOAD ==================
    @Override
    public void uploadPdf(Long noteId, User user, NotesUploadDTO dto) {
        Notes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new AuthException("Note not found"));

        log.info("Requested noteId={} by userId={}", noteId, user.getId());
        log.info("Note ownerId={}", note.getUser().getId());

        if (!note.getUser().getId().equals(user.getId())) throw new AuthException("Unauthorized access");

        dto.validate();
        PdfUtil.verifyPdf(dto.file());

        processPdfAsync(dto.file(), note);
    }

    @Async("fileTaskExecutor")
    public void processPdfAsync(MultipartFile file, Notes note) {
        try {
            Files.createDirectories(PDF_STORAGE);

            String filename = note.getId() + "_" + System.currentTimeMillis() + ".pdf";
            Path target = PDF_STORAGE.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            note.setPdfFilename(filename);
            notesRepository.save(note);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store PDF", e);
        }
    }

        @Override
        public NotesDownloadDTO downloadNoteAsPdf(Long noteId, User user) {
            Notes note = notesRepository.findById(noteId)
                    .orElseThrow(() -> new AuthException("Note not found"));

            if (!note.getUser().getId().equals(user.getId())) {
                throw new AuthException("Unauthorized access");
            }

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // ---- Author Info ----
                document.add(new Paragraph("Author Details").setBold());
                document.add(new Paragraph("First Name: " + user.getFirstName()));
                document.add(new Paragraph("Last Name: " + user.getLastName()));
                document.add(new Paragraph("Email: " + user.getEmail()));
                document.add(new Paragraph("Username: " + user.getUsername()));
                document.add(new Paragraph("Mobile: " + user.getMobile()));

                // Profile Image if exists
                if (user.getProfileImageFilename() != null) {
                    Path imgPath = Path.of("uploads/images-optimized").resolve(user.getProfileImageFilename());
                    if (Files.exists(imgPath)) {
                        Image img = new Image(ImageDataFactory.create(Files.readAllBytes(imgPath)));
                        img.setAutoScale(true);
                        document.add(img);
                    }
                }

                document.add(new Paragraph("\nNote Details").setBold());
                document.add(new Paragraph("Title: " + note.getTitle()));
                document.add(new Paragraph("Content: " + aesUtil.decrypt(note.getContent())));
                document.add(new Paragraph("Created At: " + note.getCreatedAt()));
                document.add(new Paragraph("Updated At: " + note.getUpdatedAt()));

                document.close();

                byte[] data = baos.toByteArray();
                return new NotesDownloadDTO(
                        "note_" + noteId + ".pdf",
                        "application/pdf",
                        data.length,
                        data
                );

            } catch (Exception e) {
                throw new RuntimeException("Failed to generate PDF", e);
            }
        }

    }


