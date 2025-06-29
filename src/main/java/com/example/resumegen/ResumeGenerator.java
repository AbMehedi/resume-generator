package com.example.resumegen;

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

    public void generateAndSend() throws Exception {
        // 1. Load resume data
        JSONObject resume = ResumeService.loadResume(username);

        // 2. Get user email from personal info
        if (resume.has("personal")) {
            JSONObject personal = resume.getJSONObject("personal");
            userEmail = personal.optString("email", "");
        }

        // 3. Generate HTML
        String html = TemplateEngine.generateResume(resume);

        // 4. Save HTML to file
        htmlPath = HtmlSaver.saveHtml(html, username);

        // 5. Generate PDF path
        pdfPath = htmlPath.replace(".html", ".pdf");

        // 6. Generate PDF
        PdfGenerator.generatePdf(htmlPath, pdfPath);

        // 7. Send email if email exists
        if (!userEmail.isEmpty()) {
            EmailSender.sendEmailWithAttachment(
                    userEmail,
                    "Your Generated Resume",
                    "Please find your resume attached.",
                    Paths.get(pdfPath)
            );
        }
    }

    public String getHtmlPath() {
        return htmlPath;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public String getUserEmail() {
        return userEmail;
    }
}