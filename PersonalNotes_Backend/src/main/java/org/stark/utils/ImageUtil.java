package org.stark.utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import org.imgscalr.Scalr;

public class ImageUtil {

    // === Main flexible method ===
    public static void resizeAndCompress(File sourceFile, File targetFile, int width, String format, float quality) throws IOException {
        BufferedImage img = ImageIO.read(sourceFile);
        if (img == null) {
            throw new IOException("Invalid image file: " + sourceFile.getName());
        }

        // Resize the image to specified width (keeps aspect ratio)
        BufferedImage resized = Scalr.resize(img, width);

        // Handle JPEG compression
        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            try (OutputStream os = Files.newOutputStream(targetFile.toPath())) {
                javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();

                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(quality); // 0.0f = lowest, 1.0f = highest
                }

                ImageIO.write(resized, "jpg", os);
            }
        } else {
            // For PNG, GIF, BMP etc. (no compression support here)
            ImageIO.write(resized, format, targetFile);
        }
    }

    // === Overload with defaults ===
    public static void resizeAndCompress(File sourceFile, File targetFile) throws IOException {
        // Default width = 300px, format = jpg, quality = 0.8f
        resizeAndCompress(sourceFile, targetFile, 300, "jpg", 0.8f);
    }
}
