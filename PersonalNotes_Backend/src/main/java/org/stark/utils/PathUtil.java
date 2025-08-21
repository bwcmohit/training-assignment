package org.stark.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
    public static Path resolvePath(String baseDir, String filename) throws IOException {
        Files.createDirectories(Paths.get(baseDir));
        return Paths.get(baseDir, filename);
    }
}
