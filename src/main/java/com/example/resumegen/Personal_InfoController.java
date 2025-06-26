package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;

public class Personal_InfoController {
    @FXML
    private Label label;
    @FXML
    private TextField FirstName;
    @FXML
    private TextField LastName;
    @FXML
    private TextField Email;
    @FXML
    private TextField Phone;
    @FXML
    private TextField Nationality;
    @FXML
    private TextField Address;
    @FXML
    private Button SaveAndContinue;
    @FXML
    private Button Back;

    private String username;

    // Setter method to set the username and load existing data
    public void setUsername(String username) {
        this.username = username;
        loadPersonalInfo();
    }

    // Load existing personal information from JSON
    private void loadPersonalInfo() {
        JSONObject resume = ResumeService.loadResume(username);
        if (resume.has("personal")) {
            JSONObject personal = resume.getJSONObject("personal");
            FirstName.setText(personal.optString("firstName", ""));
            LastName.setText(personal.optString("lastName", ""));
            Email.setText(personal.optString("email", ""));
            Phone.setText(personal.optString("phone", ""));
            Nationality.setText(personal.optString("nationality", ""));
            Address.setText(personal.optString("address", ""));
        }
    }

    // Validate required fields
    private boolean validateInputs() {
        if (FirstName.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }
        if (LastName.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }
        if (Email.getText().trim().isEmpty()) {
            showError("Email is required");
            return false;
        }
        if (!isValidEmail(Email.getText().trim())) {
            showError("Invalid email format");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.contains("@") && email.contains(".");
    }

    private void showError(String message) {
        label.setText(message);
        label.setVisible(true);
    }

    @FXML
    public void backToDashBoard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        DashboardController dashboardController = fxmlLoader.getController();
        dashboardController.setUsername(username);

        Stage stage = (Stage) Back.getScene().getWindow();
        stage.setTitle("Dashboard");
        stage.setScene(scene);
    }

    @FXML
    public void saveAndContinue(ActionEvent event) throws IOException {
        // Hide any previous errors
        label.setVisible(false);

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Save data to JSON
        savePersonalInfo();

        // Navigate to Education screen
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Education.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        EducationController educationController = fxmlLoader.getController();
        educationController.setUsername(username);

        Stage stage = (Stage) SaveAndContinue.getScene().getWindow();
        stage.setTitle("Education");
        stage.setScene(scene);
    }

    private void savePersonalInfo() {
        // Get the existing resume data
        JSONObject resume = ResumeService.loadResume(username);

        // Create personal info JSON object
        JSONObject personal = new JSONObject();
        personal.put("firstName", FirstName.getText().trim());
        personal.put("lastName", LastName.getText().trim());
        personal.put("email", Email.getText().trim());
        personal.put("phone", Phone.getText().trim());
        personal.put("nationality", Nationality.getText().trim());
        personal.put("address", Address.getText().trim());

        // Add personal info to resume
        resume.put("personal", personal);

        // Save to file
        ResumeService.saveResume(username, resume);
    }
}