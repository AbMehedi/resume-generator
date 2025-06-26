package com.example.resumegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HtmlSaver {
    public static String saveHtml(String html, String username) throws IOException {
        // Sanitize username for filename
        String safeUsername = (username == null || username.isEmpty())
                ? "unknown"
                : username.replaceAll("[^a-zA-Z0-9_-]", "_");

        // Create timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // Create filename
        String fileName = safeUsername + "_resume_" + timestamp + ".html";

        // Write to file
        Path path = Paths.get(fileName);
        Files.write(path, html.getBytes());

        return path.toAbsolutePath().toString();
    }
}