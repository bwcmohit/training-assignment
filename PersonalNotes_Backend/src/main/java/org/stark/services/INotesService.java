package org.stark.services;

import org.stark.dtos.NotesDownloadDTO;
import org.stark.dtos.NotesRequestDTO;
import org.stark.dtos.NotesResponseDTO;
import org.stark.dtos.NotesUploadDTO;
import org.stark.entities.User;

import java.util.List;



    public interface INotesService {

        // Existing text note methods
        NotesResponseDTO createNote(NotesRequestDTO dto, User user);
        NotesResponseDTO updateNote(Long noteId, NotesRequestDTO dto, User user);
        void deleteNote(Long noteId, User user);
        List<NotesResponseDTO> getNotes(User user);
        List<NotesResponseDTO> searchNotes(User user, String keyword);

        // ===== PDF methods =====
        void uploadPdf(Long noteId, User user, NotesUploadDTO dto);
        NotesDownloadDTO downloadNoteAsPdf(Long noteId, User user);
    }



