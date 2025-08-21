package org.stark.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserDTO(

        @NotBlank(message = "First Name is required")
        @Size(max=50)
        String firstName,

        String lastName,

        @NotBlank(message = "Unique Email is required")
        @Email(message = "Email pattern does not match")
        @Size(max=50)
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 16, message = "Password size must be between 8 to 16 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
                message = "Password must contain at least one uppercase letter, one number, and one special character"
        )
        String password,

        @NotBlank(message ="Username is required")
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,20}$",
                message = "Username must be 3-20 characters and contain only letters, numbers, dots, underscores, or hyphens")
        String username,

        @NotBlank(message ="Mobile Number is required")
        @Pattern(regexp = "^[6-9]\\d{9}$",
                message = "Invalid mobile number. Must be 10 digits starting with 6-9")
        String mobile
        )
{}
