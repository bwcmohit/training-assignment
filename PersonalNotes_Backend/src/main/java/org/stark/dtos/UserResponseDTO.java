package org.stark.dtos;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String username,
        String mobile,
        org.stark.enums.Roles role
) {}
