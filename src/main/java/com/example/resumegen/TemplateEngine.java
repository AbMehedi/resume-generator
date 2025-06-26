package com.example.resumegen;

import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine {
    private static final String TEMPLATE_DIR = "src/main/resources/templates/";

    public static String generateResume(JSONObject resumeData) throws IOException {
        // Load the default template
        String templateContent = new String(Files.readAllBytes(
                Paths.get(TEMPLATE_DIR + "modern_dark.html")
        ));

        // Create data model
        Map<String, String> dataModel = createDataModel(resumeData);

        // Replace placeholders
        return replacePlaceholders(templateContent, dataModel);
    }

    private static Map<String, String> createDataModel(JSONObject resume) {
        Map<String, String> model = new HashMap<>();

        // Personal Info
        if (resume.has("personal")) {
            JSONObject personal = resume.getJSONObject("personal");
            addToModel(model, "personal", personal);
            model.put("initials", getInitials(personal));
        }

        // Education
        if (resume.has("education")) {
            JSONObject education = resume.getJSONObject("education");
            addToModel(model, "education", education);
        }

        // Skills
        if (resume.has("skills")) {
            JSONObject skills = resume.getJSONObject("skills");
            addToModel(model, "skills", skills);
        }

        // Experience
        if (resume.has("experience")) {
            JSONObject experience = resume.getJSONObject("experience");
            addToModel(model, "experience", experience);
        }

        return model;
    }

    private static String getInitials(JSONObject personal) {
        String firstName = personal.optString("firstName", "");
        String lastName = personal.optString("lastName", "");

        StringBuilder initials = new StringBuilder();
        if (!firstName.isEmpty()) initials.append(firstName.charAt(0));
        if (!lastName.isEmpty()) initials.append(lastName.charAt(0));

        return initials.toString().toUpperCase();
    }

    private static void addToModel(Map<String, String> model, String prefix, JSONObject data) {
        for (String key : data.keySet()) {
            String value = data.optString(key, "");
            model.put(prefix + "." + key, escapeHtml(value));
        }
    }

    private static String replacePlaceholders(String template, Map<String, String> data) {
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            String replacement = data.getOrDefault(placeholder, "");
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String escapeHtml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}