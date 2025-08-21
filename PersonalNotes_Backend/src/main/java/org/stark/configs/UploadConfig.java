package org.stark.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class UploadConfig {

    private final FileStorageConfig storageConfig;

    public UploadConfig(FileStorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }

    @PostConstruct
    public void init() {
        // Collect all directories from storage config
        String[] dirs = {
                storageConfig.getImages(),
                storageConfig.getImagesOptimized(),
                storageConfig.getDocs()
        };

        for (String dirPath : dirs) {
            if (dirPath == null || dirPath.isEmpty()) {
                System.err.println("Directory path is not set in FileStorageConfig!");
                continue;
            }

            File dir = new File(dirPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println(created ? "Created: " + dir.getAbsolutePath() : "Failed: " + dir.getAbsolutePath());
            } else {
                System.out.println("Exists: " + dir.getAbsolutePath());
            }
        }
    }
}
