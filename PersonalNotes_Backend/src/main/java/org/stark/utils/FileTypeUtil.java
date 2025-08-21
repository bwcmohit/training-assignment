package org.stark.utils;

public class FileTypeUtil {

    private FileTypeUtil() {}

    public static String getImageExtension(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("File content type is missing");
        }

        switch (contentType.toLowerCase()) {
            case "image/png":
                return ".png";
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            default:
                throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }
}
