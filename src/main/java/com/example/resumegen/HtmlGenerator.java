package com.example.resumegen;

import org.json.JSONObject;

public class HtmlGenerator {
    public static String generate(JSONObject resume) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
                .append("<html lang='en'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<title>Professional Resume</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; line-height: 1.6; }")
                .append(".header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px; }")
                .append(".section { margin-top: 20px; }")
                .append(".section-title { font-size: 1.3em; border-bottom: 1px solid #333; padding-bottom: 5px; }")
                .append("</style>")
                .append("</head>")
                .append("<body>");

        // Personal Info
        if (resume.has("personal")) {
            JSONObject personal = resume.getJSONObject("personal");
            html.append("<div class='header'>")
                    .append("<h1>").append(personal.optString("firstName", "")).append(" ")
                    .append(personal.optString("lastName", "")).append("</h1>")
                    .append("<p>Email: ").append(personal.optString("email", "")).append(" | ")
                    .append("Phone: ").append(personal.optString("phone", "")).append("</p>")
                    .append("<p>").append(personal.optString("nationality", "")).append(" | ")
                    .append(personal.optString("address", "")).append("</p>")
                    .append("</div>");
        }

        // Education
        if (resume.has("education")) {
            JSONObject edu = resume.getJSONObject("education");
            html.append("<div class='section'>")
                    .append("<h2 class='section-title'>Education</h2>")
                    .append("<h3>").append(edu.optString("collegeName", "")).append("</h3>")
                    .append("<p>").append(edu.optString("collegeDegree", "")).append(" | ")
                    .append("Year: ").append(edu.optString("collegePassingYear", "")).append(" | ")
                    .append("CGPA: ").append(edu.optString("collegeCGPA", "")).append("</p>")
                    .append("<h3>").append(edu.optString("universityName", "")).append("</h3>")
                    .append("<p>").append(edu.optString("universityDegree", "")).append(" | ")
                    .append("Year: ").append(edu.optString("universityPassingYear", "")).append(" | ")
                    .append("CGPA: ").append(edu.optString("universityCGPA", "")).append("</p>")
                    .append("</div>");
        }

        // Skills
        if (resume.has("skills")) {
            JSONObject skills = resume.getJSONObject("skills");
            html.append("<div class='section'>")
                    .append("<h2 class='section-title'>Skills</h2>")
                    .append("<ul>")
                    .append("<li>").append(skills.optString("skill1", "")).append("</li>")
                    .append("<li>").append(skills.optString("skill2", "")).append("</li>")
                    .append("<li>").append(skills.optString("skill3", "")).append("</li>")
                    .append("<li>").append(skills.optString("skill4", "")).append("</li>")
                    .append("</ul>")
                    .append("</div>");
        }

        // Experience
        if (resume.has("experience")) {
            JSONObject exp = resume.getJSONObject("experience");
            html.append("<div class='section'>")
                    .append("<h2 class='section-title'>Work Experience</h2>")
                    .append("<h3>").append(exp.optString("company1", "")).append("</h3>")
                    .append("<p>Position: ").append(exp.optString("jobTitle1", "")).append("</p>")
                    .append("<h3>").append(exp.optString("company2", "")).append("</h3>")
                    .append("<p>Position: ").append(exp.optString("jobTitle2", "")).append("</p>")
                    .append("</div>");
        }

        html.append("</body></html>");
        return html.toString();
    }
}