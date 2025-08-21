package org.stark.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record NotesDownloadDTO(

        @NotBlank(message = "Filename is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$", // Prevent directory traversal & strange chars
                message = "Invalid filename format"
        )
        String filename,

        @NotBlank(message = "Content type is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9!#$&^_.+-]+/[a-zA-Z0-9!#$&^_.+-]+$",
                message = "Invalid MIME type format"
        )
        String contentType,

        @Positive(message = "File size must be positive")
        long size,

        byte[] data

) { }
