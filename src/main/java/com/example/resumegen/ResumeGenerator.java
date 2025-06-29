package com.example.resumegen;

import javafx.application.Platform;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

public class ResumeGenerator {
    private final String username;
    private String htmlPath;
    private String pdfPath;
    private String userEmail;

    public ResumeGenerator(String username) {
        this.username = username;
    }

    public void generateAndOpen() throws Exception {
        JSONObject resume = ResumeService.loadResume(username);

        if (resume.has("personal")) {
            JSONObject personal = resume.getJSONObject("personal");
            userEmail = personal.optString("email", "");
        }

        String html = TemplateEngine.generateResume(resume);
        htmlPath = HtmlSaver.saveHtml(html, username);
        pdfPath = htmlPath.replace(".html", ".pdf");

        PdfGenerator.generatePdf(htmlPath, pdfPath);

        // Open PDF immediately after generation
        openPdfFile();
    }

    public void sendEmail() {
        if (!userEmail.isEmpty()) {
            try {
                EmailSender.sendEmailWithAttachment(
                        userEmail,
                        "Your Generated Resume",
                        "Please find your resume attached.",
                        Paths.get(pdfPath)
                );
            } catch (IOException e) {
                System.err.println("Email sending failed: " + e.getMessage());
            }
        }
    }

    private void openPdfFile() {
        File pdfFile = new File(pdfPath);
        if (pdfFile.exists() && Desktop.isDesktopSupported()) {
            Platform.runLater(() -> {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (IOException e) {
                    System.err.println("Error opening PDF: " + e.getMessage());
                }
            });
        }
    }

    public String getHtmlPath() { return htmlPath; }
    public String getPdfPath() { return pdfPath; }
    public String getUserEmail() { return userEmail; }
}