package com.example.resumegen;

import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResumeService {
    public static void saveResume(String username, JSONObject resumeData) {
        try {
            String filename = username + "_resume.json";
            Files.write(Paths.get(filename), resumeData.toString(2).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject loadResume(String username) {
        try {
            String filename = username + "_resume.json";
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            return new JSONObject(content);
        } catch (IOException e) {
            return new JSONObject(); // Return empty object if file doesn't exist
        }
    }
}