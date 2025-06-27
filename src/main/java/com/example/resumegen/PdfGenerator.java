package com.example.resumegen;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class PdfGenerator {

    // Method to generate PDF from HTML file
    public static void generatePdf(String htmlFilePath, String pdfOutputPath) throws Exception {
        // Start a new thread to ensure Chrome waits until HTML is fully loaded
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Runnable task to wait for HTML to fully load and then generate PDF
        Runnable waitForHtmlGeneration = new Runnable() {
            @Override
            public void run() {
                try {
                    // Wait for a few seconds to ensure dynamic content is fully loaded
                    System.out.println("Waiting for HTML to fully load...");
                    Thread.sleep(3000); // Wait for 3 seconds (you can adjust as needed)
                    System.out.println("Proceeding to generate PDF...");

                    // Ensure Chrome path is correct for your system
                    String chromePath = getChromePath(); // Calls the method to get Chrome path
                    if (chromePath == null) {
                        System.err.println("Chrome path is not found. Exiting...");
                        return;
                    }

                    // Ensure the HTML file is completely written and closed
                    Path htmlFile = Paths.get(htmlFilePath);
                    if (Files.exists(htmlFile)) {
                        // Check if the file is not locked (this is just a simple check)
                        try (BufferedReader reader = Files.newBufferedReader(htmlFile)) {
                            reader.readLine(); // Attempt to read the first line
                        } catch (IOException e) {
                            System.err.println("HTML file is still locked or not ready.");
                            return; // Exit if the file is locked
                        }
                    }

                    // Build Chrome command to convert HTML to PDF
                    ProcessBuilder pb = new ProcessBuilder(
                            chromePath,
                            "--headless", "--disable-gpu", "--no-sandbox",
                            "--run-all-compositor-stages-before-draw",
                            "--print-to-pdf=" + pdfOutputPath,
                            "file://" + htmlFilePath
                    );

                    // Capture the error and output streams
                    pb.redirectErrorStream(true);

                    // Start the Chrome process to generate the PDF
                    Process process = pb.start();

                    // Capture output for debugging
                    StringBuilder output = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                        }
                    }

                    // Wait for the Chrome process to complete
                    int exitCode = process.waitFor();

                    // If the process fails, throw an exception
                    if (exitCode != 0) {
                        throw new IOException("Chrome PDF generation failed with exit code " + exitCode + ": " + output);
                    }

                    System.out.println("PDF generated successfully at: " + pdfOutputPath);

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Submit the task to the executor
        executor.submit(waitForHtmlGeneration);
        executor.shutdown(); // Shut down the executor after the task is complete
    }

    // Method to determine the path of Chrome based on the operating system
    private static String getChromePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            if (Files.exists(Paths.get("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"))) {
                return "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
            }
            if (Files.exists(Paths.get("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"))) {
                return "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
            }
        } else if (os.contains("mac")) {
            return "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
        } else if (os.contains("linux")) {
            return "google-chrome";
        }
        return null; // Chrome path not found
    }


}
