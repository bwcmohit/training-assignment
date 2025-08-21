package org.stark.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDTO(

        @NotBlank(message = "Username or Email is required")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-@]+$", // Allows alphanumerics, ., _, %, +, -, @
                message = "Invalid characters in username or email"
        )
        String usernameOrEmail,

        @NotBlank(message = "Password is required")
        String password

) { }
