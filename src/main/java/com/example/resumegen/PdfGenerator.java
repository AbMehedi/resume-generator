package com.example.resumegen;

import java.io.*;
import java.nio.file.*;

public class PdfGenerator {

    // Synchronous method to generate PDF from HTML file
    public static void generatePdf(String htmlFilePath, String pdfOutputPath) throws Exception {
        String chromePath = getChromePath();
        if (chromePath == null) {
            throw new IOException("Chrome path not found");
        }

        // Build Chrome command to convert HTML to PDF
        ProcessBuilder pb = new ProcessBuilder(
                chromePath,
                "--headless", "--disable-gpu", "--no-sandbox",
                "--run-all-compositor-stages-before-draw",
                "--print-to-pdf=" + pdfOutputPath,
                "file://" + htmlFilePath
        );

        // Redirect output streams
        pb.redirectErrorStream(true);

        // Start the Chrome process to generate the PDF
        Process process = pb.start();

        // Read output for debugging
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Wait for the Chrome process to complete
        int exitCode = process.waitFor();

        // Verify PDF was created
        if (exitCode != 0 || !Files.exists(Paths.get(pdfOutputPath))) {
            throw new IOException("PDF generation failed with exit code " + exitCode + ": " + output);
        }

        System.out.println("PDF generated successfully at: " + pdfOutputPath);
    }

    // Method to determine the path of Chrome based on the operating system
    private static String getChromePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Check both possible paths on Windows
            Path path1 = Paths.get("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
            Path path2 = Paths.get("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
            if (Files.exists(path1)) {
                return path1.toString();
            } else if (Files.exists(path2)) {
                return path2.toString();
            }
        } else if (os.contains("mac")) {
            return "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
        } else if (os.contains("linux")) {
            return "google-chrome";
        }
        return null; // Chrome path not found
    }
}