package com.example.resumegen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResumeService {
    public static void saveResume(String username, JSONObject resumeData) {
        try {
            String filename = username + "_resume.json";
            String formattedJson = resumeData.toString(2);
            Files.write(Paths.get(filename), formattedJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject loadResume(String username) {
        try {
            String filename = username + "_resume.json";
            if (!Files.exists(Paths.get(filename))) {
                return new JSONObject();
            }

            String content = new String(Files.readAllBytes(Paths.get(filename)));

            // Handle empty files
            if (content.trim().isEmpty()) {
                return new JSONObject();
            }

            return new JSONObject(new JSONTokener(content));
        } catch (IOException e) {
            return new JSONObject();
        }
    }
}