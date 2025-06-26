package com.example.resumegen;

import org.json.JSONArray;
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
        // Load the HTML template file
        String templateContent = new String(Files.readAllBytes(
                Paths.get(TEMPLATE_DIR + "project_template.html")
        ));

        // Create data model with all resume sections
        Map<String, String> dataModel = createDataModel(resumeData);

        // Replace placeholders with actual data
        return replacePlaceholders(templateContent, dataModel);
    }

    private static Map<String, String> createDataModel(JSONObject resume) {
        Map<String, String> model = new HashMap<>();

        // Personal Information Section
        if (resume.has("personal")) {
            JSONObject personal = resume.getJSONObject("personal");
            addToModel(model, "personal", personal);
            model.put("initials", getInitials(personal));

            // Set job title if available, otherwise use default
            if (personal.has("jobTitle")) {
                model.put("personal.jobTitle", escapeHtml(personal.getString("jobTitle")));
            } else {
                model.put("personal.jobTitle", "Professional");
            }

            // Process About Me section
            if (personal.has("aboutMe")) {
                String aboutMe = personal.optString("aboutMe", "");
                aboutMe = escapeHtml(aboutMe);
                aboutMe = aboutMe.replace("\n", "<br>");
                model.put("aboutMe", aboutMe);
            } else {
                model.put("aboutMe", "Passionate professional with extensive experience in my field.");
            }
        }

        // Education Section
        if (resume.has("education")) {
            JSONObject education = resume.getJSONObject("education");
            addToModel(model, "education", education);

            // Generate HTML for education timeline
            StringBuilder educationHtml = new StringBuilder();
            addEducationItem(educationHtml, education,
                    "collegeName", "collegeDegree",
                    "collegePassingYear", "collegeCGPA");
            addEducationItem(educationHtml, education,
                    "universityName", "universityDegree",
                    "universityPassingYear", "universityCGPA");
            model.put("educationSection", educationHtml.toString());
        }

        // Skills Section
        if (resume.has("skills")) {
            JSONArray skillsArray = resume.getJSONArray("skills");
            StringBuilder skillsHtml = new StringBuilder();

            for (int i = 0; i < skillsArray.length(); i++) {
                JSONObject skill = skillsArray.getJSONObject(i);
                String name = skill.optString("name", "");
                String percent = skill.optString("percent", "0");

                if (!name.isEmpty()) {
                    skillsHtml.append("<div class=\"skill-item\">")
                            .append("<div class=\"skill-header\">")
                            .append("<div class=\"skill-name\">").append(escapeHtml(name)).append("</div>")
                            .append("</div>")
                            .append("<div class=\"skill-bar\">")
                            .append("<div class=\"skill-level\" style=\"width: ").append(percent).append("%\"></div>")
                            .append("</div>")
                            .append("</div>");
                }
            }
            model.put("skillsSection", skillsHtml.toString());
        }

        // Languages Section
        if (resume.has("languages")) {
            JSONArray languages = resume.getJSONArray("languages");
            StringBuilder languagesHtml = new StringBuilder();

            for (int i = 0; i < languages.length(); i++) {
                JSONObject lang = languages.getJSONObject(i);
                String name = lang.optString("name", "");
                String proficiency = lang.optString("proficiency", "");

                if (!name.isEmpty()) {
                    languagesHtml.append("<div class=\"skill-item\">")
                            .append("<div class=\"skill-header\">")
                            .append("<div class=\"skill-name\">").append(escapeHtml(name)).append(" (").append(proficiency).append(")</div>")
                            .append("</div>")
                            .append("<div class=\"skill-bar\">")
                            .append("<div class=\"skill-level\" style=\"width: ").append(getProficiencyWidth(proficiency)).append("%\"></div>")
                            .append("</div>")
                            .append("</div>");
                }
            }
            model.put("languagesSection", languagesHtml.toString());
        }

        // Experience Section
        if (resume.has("experience")) {
            JSONObject exp = resume.getJSONObject("experience");
            StringBuilder experienceHtml = new StringBuilder();

            addExperienceItem(experienceHtml, exp, "company1", "jobTitle1", "jobDescription1");
            addExperienceItem(experienceHtml, exp, "company2", "jobTitle2", "jobDescription2");

            model.put("experienceSection", experienceHtml.toString());
        }

        // Interests Section
        if (resume.has("interests")) {
            JSONArray interests = resume.getJSONArray("interests");
            StringBuilder interestsHtml = new StringBuilder();

            for (int i = 0; i < interests.length(); i++) {
                String interest = interests.getString(i);
                if (!interest.isEmpty()) {
                    interestsHtml.append("<div class=\"interest-item\">")
                            .append(escapeHtml(interest))
                            .append("</div>");
                }
            }
            model.put("interestsSection", interestsHtml.toString());
        }

        return model;
    }

    private static void addEducationItem(StringBuilder html, JSONObject education,
                                         String nameKey, String degreeKey,
                                         String yearKey, String cgpaKey) {
        String name = education.optString(nameKey, "");
        String degree = education.optString(degreeKey, "");
        String year = education.optString(yearKey, "");
        String cgpa = education.optString(cgpaKey, "");

        if (!name.isEmpty()) {
            html.append("<div class=\"timeline-item\">")
                    .append("<div class=\"timeline-header\">")
                    .append("<div class=\"timeline-title\">").append(escapeHtml(name)).append("</div>")
                    .append("<div class=\"timeline-date\">").append(escapeHtml(year)).append("</div>")
                    .append("</div>")
                    .append("<div class=\"timeline-subtitle\">").append(escapeHtml(degree)).append("</div>")
                    .append("<div class=\"timeline-content\">")
                    .append("<p>CGPA: ").append(escapeHtml(cgpa)).append("</p>")
                    .append("</div>")
                    .append("</div>");
        }
    }

    private static void addExperienceItem(StringBuilder html, JSONObject exp,
                                          String companyKey, String titleKey, String descKey) {
        String company = exp.optString(companyKey, "");
        String title = exp.optString(titleKey, "");
        String description = exp.optString(descKey, "");

        if (!company.isEmpty()) {
            html.append("<div class=\"timeline-item\">")
                    .append("<div class=\"timeline-header\">")
                    .append("<div class=\"timeline-title\">").append(escapeHtml(company)).append("</div>")
                    .append("</div>")
                    .append("<div class=\"timeline-subtitle\">").append(escapeHtml(title)).append("</div>")
                    .append("<div class=\"timeline-content\">");

            // Process job description with line breaks
            if (!description.isEmpty()) {
                String[] lines = description.split("\\n");
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        html.append("<p>").append(escapeHtml(line)).append("</p>");
                    }
                }
            }

            html.append("</div>")
                    .append("</div>");
        }
    }

    private static int getProficiencyWidth(String proficiency) {
        switch (proficiency.toLowerCase()) {
            case "native": return 100;
            case "fluent": return 80;
            case "intermediate": return 60;
            case "basic": return 40;
            default: return 60;
        }
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
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
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