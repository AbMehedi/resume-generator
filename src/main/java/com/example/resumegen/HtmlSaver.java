package com.example.resumegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HtmlSaver {

    // Method to save the HTML file
    public static String saveHtml(String html, String username) throws IOException {
        // Sanitize username
        String safeUsername = (username == null || username.isEmpty())
                ? "unknown"
                : username.replaceAll("[^a-zA-Z0-9_-]", "_");

        // Create timestamp for unique filename
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // Create filename
        String fileName = safeUsername + "_resume_" + timestamp + ".html";

        // Set the directory path where the file will be saved
        Path documentsDir = Paths.get(System.getProperty("user.home"), "Documents");
        Path resumeDir = documentsDir.resolve("ResumeGenerator");

        // Create the directory if it doesn't exist
        if (!Files.exists(resumeDir)) {
            Files.createDirectories(resumeDir);
        }

        // Define the full path to save the HTML file
        Path filePath = resumeDir.resolve(fileName);

        // Write the HTML content to the file
        Files.write(filePath, html.getBytes());

        // Close the file and return the absolute path
        return filePath.toAbsolutePath().toString();
    }
}
