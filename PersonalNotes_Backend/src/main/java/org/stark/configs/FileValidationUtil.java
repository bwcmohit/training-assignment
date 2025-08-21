package org.stark.configs;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileValidationUtil {

    public static void validateImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file");
        if (!file.getContentType().startsWith("image/"))
            throw new IllegalArgumentException("Invalid image type");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new IllegalArgumentException("Image size exceeds 2MB");
    }

    public static void validatePdf(MultipartFile file) throws IOException {
        if (!"application/pdf".equals(file.getContentType()))
            throw new IllegalArgumentException("Only PDFs allowed");
        if (file.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("PDF exceeds 5MB");
    }
}
