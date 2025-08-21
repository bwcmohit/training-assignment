package org.stark.dtos;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record NotesUploadDTO(

        @NotNull(message = "PDF file is required")
        MultipartFile file

) {
    public void validate() {
        if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
            throw new IllegalArgumentException("PDF file must be <= 5MB");
        }
        if (!file.getContentType().matches("application/(pdf|x-pdf)")) {
            throw new IllegalArgumentException("Only PDF format is allowed");
        }

    }
}
