package org.stark.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stark.dtos.NotesDownloadDTO;
import org.stark.dtos.NotesRequestDTO;
import org.stark.dtos.NotesResponseDTO;
import org.stark.dtos.NotesUploadDTO;
import org.stark.entities.User;
import org.stark.services.INotesService;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NotesController {

    private final INotesService notesService;

    public NotesController(INotesService notesService) {
        this.notesService = notesService;
    }

    @PostMapping
    public ResponseEntity<NotesResponseDTO> createNote(
            @AuthenticationPrincipal User user,
            @RequestBody NotesRequestDTO dto) {

        return ResponseEntity.ok(notesService.createNote(dto, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotesResponseDTO> updateNote(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody NotesRequestDTO dto) {

        return ResponseEntity.ok(notesService.updateNote(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        notesService.deleteNote(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<NotesResponseDTO>> getAllNotes(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(notesService.getNotes(user));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NotesResponseDTO>> searchNotes(
            @AuthenticationPrincipal User user,
            @RequestParam String keyword) {

        return ResponseEntity.ok(notesService.searchNotes(user, keyword));
    }

    @PostMapping("/{id}/upload-file")
    public ResponseEntity<String> uploadNoteFile(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        NotesUploadDTO dto = new NotesUploadDTO(file); // wrap MultipartFile
        notesService.uploadPdf(id, user, dto);         // call correct method
        return ResponseEntity.accepted().body("File upload started.");
    }

    @GetMapping("/{id}/download-file")
    public ResponseEntity<byte[]> downloadNoteAsPdf(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        // Call the service method that generates a PDF with note + author details
        NotesDownloadDTO fileData = notesService.downloadNoteAsPdf(id, user);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileData.filename() + "\"")
                .header("Content-Type", fileData.contentType())
                .header("Content-Length", String.valueOf(fileData.size()))
                .body(fileData.data());
    }





}
