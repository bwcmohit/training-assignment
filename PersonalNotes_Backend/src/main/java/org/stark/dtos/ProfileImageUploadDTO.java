package org.stark.dtos;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ProfileImageUploadDTO(

        @NotNull(message = "Profile image file is required")
        MultipartFile file

) {
    // Extra method-level validation for size and format
    public void validate() {
        if (file.getSize() > 2 * 1024 * 1024) { // 2 MB
            throw new IllegalArgumentException("Profile image must be <= 2MB");
        }
        if (!file.getContentType().toLowerCase().matches("image/(jpeg|png)")) {
            throw new IllegalArgumentException("Profile image must be JPEG or PNG");
        }
    }
}
