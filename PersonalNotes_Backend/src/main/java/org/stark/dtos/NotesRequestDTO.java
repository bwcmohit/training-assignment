package org.stark.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotesRequestDTO(

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Content is required")
        @Size(max = 10_000, message = "Content cannot exceed 10,000 characters")
        String content

) {}
