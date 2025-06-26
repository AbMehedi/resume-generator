package com.example.resumegen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserService {
    private static final String USER_DB = "users.json";

    public static void initUserDatabase() {
        try {
            if (!Files.exists(Paths.get(USER_DB))) {
                Files.write(Paths.get(USER_DB), new JSONArray().toString().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean createUser(String username, String email, String password) {
        try {
            JSONArray users = getUsers();

            // Check for existing user
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("username").equals(username)) {
                    return false; // Username exists
                }
                if (user.getString("email").equals(email)) {
                    return false; // Email exists
                }
            }

            // Create new user
            JSONObject newUser = new JSONObject();
            newUser.put("username", username);
            newUser.put("email", email);
            newUser.put("password", BCrypt.hashpw(password, BCrypt.gensalt()));

            users.put(newUser);
            saveUsers(users);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateUser(String identifier, String password) {
        try {
            JSONArray users = getUsers();
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if ((user.getString("username").equals(identifier) ||
                        user.getString("email").equals(identifier)) &&
                        BCrypt.checkpw(password, user.getString("password"))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static JSONArray getUsers() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(USER_DB)));
        return new JSONArray(content);
    }

    private static void saveUsers(JSONArray users) throws IOException {
        Files.write(Paths.get(USER_DB), users.toString(2).getBytes());
    }
}