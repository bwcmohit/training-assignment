package org.stark.dtos;

import java.time.Instant;
import java.time.LocalDateTime;

public record NotesResponseDTO(
        Long id,
        String title,
        String content,
        String ownerUsername,
        Instant createdAt,
        Instant updatedAt
) {
}
