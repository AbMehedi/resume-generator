package com.example.resumegen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine {
    private static final String TEMPLATE_DIR = "src/main/resources/templates/";

    public static String generateResume(JSONObject resumeData) throws IOException {
        String templateContent = new String(
                TemplateEngine.class.getResourceAsStream("/templates/project_template.html").readAllBytes()
        );
        // Create data model
        Map<String, String> dataModel = createDataModel(resumeData);

        // Replace placeholders
        return replacePlaceholders(templateContent, dataModel);
    }

    private static Map<String, String> createDataModel(JSONObject resume) {
        Map<String, String> model = new HashMap<>();

        // Initialize profile picture and initials
        model.put("profilePicture", "");
        model.put("initials", "");

        // Personal Information Section
        if (resume.has("personal")) {
            JSONObject personal = resume.getJSONObject("personal");
            addToModel(model, "personal", personal);

            // Set initials
            model.put("initials", getInitials(personal));

            // Handle profile picture
            if (personal.has("profilePicture")) {
                String base64Image = personal.getString("profilePicture");
                if (!base64Image.isEmpty()) {
                    // Detect image type
                    String mimeType = "image/jpeg"; // Default to JPEG
                    if (base64Image.startsWith("iVBORw")) {
                        mimeType = "image/png"; // PNG detection
                    }
                    model.put("profilePicture", "data:" + mimeType + ";base64," + base64Image);
                }
            }

            // Set job title if available, otherwise use default
            if (personal.has("jobTitle")) {
                model.put("personal.jobTitle", escapeHtml(personal.getString("jobTitle")));
            } else {
                model.put("personal.jobTitle", "Professional");
            }
            if (personal.has("nationality")) {
                model.put("personal.nationality", escapeHtml(personal.getString("nationality")));
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

            addExperienceItem(experienceHtml, exp, "company1", "jobTitle1", "jobDescription1", "startYear1", "endYear1");
            addExperienceItem(experienceHtml, exp, "company2", "jobTitle2", "jobDescription2", "startYear2", "endYear2");

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
                                          String companyKey, String titleKey,
                                          String descKey, String startYearKey, String endYearKey) {
        String company = exp.optString(companyKey, "");
        String title = exp.optString(titleKey, "");
        String description = exp.optString(descKey, "");
        String startYear = exp.optString(startYearKey, "");
        String endYear = exp.optString(endYearKey, "");

        if (!company.isEmpty()) {
            html.append("<div class=\"timeline-item\">")
                    .append("<div class=\"timeline-header\">")
                    .append("<div class=\"timeline-title\">").append(escapeHtml(company)).append("</div>")
                    .append("<div class=\"timeline-date\">").append(escapeHtml(startYear)).append(" - ").append(escapeHtml(endYear)).append("</div>")
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