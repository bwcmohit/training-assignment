package org.stark.dtos;

public record LoginResponseDTO(
        long userId,
        String token,
        String expiryMessage
) {}
