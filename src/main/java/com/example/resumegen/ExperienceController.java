package com.example.resumegen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ExperienceController {
    @FXML private TextField Company1;
    @FXML private TextField JobTitle1;
    @FXML private TextArea JobDescription1;
    @FXML private TextField Company2;
    @FXML private TextField JobTitle2;
    @FXML private TextArea JobDescription2;
    @FXML private Button NextButton;
    @FXML private Button Back;
    @FXML private Label label;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        loadExperienceData();
    }

    @FXML
    public void initialize() {
        // Configure capitalization for single-line fields
        configureCapitalization(Company1);
        configureCapitalization(JobTitle1);
        configureCapitalization(Company2);
        configureCapitalization(JobTitle2);

        // Configure sentence capitalization for job descriptions
        configureSentenceCapitalization(JobDescription1);
        configureSentenceCapitalization(JobDescription2);
    }

    private void configureCapitalization(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isAdded() || change.isReplaced()) {
                int start = change.getRangeStart();
                if (start == 0) {
                    String newText = change.getText();
                    if (!newText.isEmpty()) {
                        change.setText(newText.substring(0, 1).toUpperCase() +
                                (newText.length() > 1 ? newText.substring(1) : ""));
                    }
                }
            }
            return change;
        }));
    }

    private void configureSentenceCapitalization(TextArea area) {
        area.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isAdded()) {
                String text = change.getText();
                String prevText = change.getControlText();
                int start = change.getRangeStart();

                // Capitalize if:
                // 1. At start of text
                // 2. After sentence-ending punctuation
                if (start == 0 ||
                        (start >= 2 &&
                                Pattern.compile("[.!?]\\s+$").matcher(prevText.substring(0, start)).find())) {
                    if (!text.isEmpty()) {
                        change.setText(text.substring(0, 1).toUpperCase() +
                                (text.length() > 1 ? text.substring(1) : ""));
                    }
                }
            }
            return change;
        }));
    }


    private void loadExperienceData() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("experience")) {
                JSONObject exp = resume.getJSONObject("experience");
                Company1.setText(exp.optString("company1", ""));
                JobTitle1.setText(exp.optString("jobTitle1", ""));
                JobDescription1.setText(exp.optString("jobDescription1", ""));
                Company2.setText(exp.optString("company2", ""));
                JobTitle2.setText(exp.optString("jobTitle2", ""));
                JobDescription2.setText(exp.optString("jobDescription2", ""));
            }
        } catch (Exception e) {
            showError("Error loading experience data: " + e.getMessage());
        }
    }

    @FXML
    public void backToSkills(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Skills.fxml"));
        Scene scene = new Scene(loader.load());

        SkillsController controller = loader.getController();
        controller.setUsername(username);

        Stage stage = (Stage) Back.getScene().getWindow();
        stage.setTitle("Skills");
        stage.setScene(scene);
    }

    @FXML
    public void nextToLanguages(ActionEvent event) {
        if (validateExperience()) {
            saveExperience();
            navigateToLanguagesPage(event);
        }
    }

    private boolean validateExperience() {
        // Validate company 1 fields
        if (!Company1.getText().isBlank()) {
            if (JobTitle1.getText().isBlank()) {
                showError("Job title is required for company 1");
                return false;
            }
            if (JobDescription1.getText().isBlank()) {
                showError("Job description is required for company 1");
                return false;
            }
        }

        // Validate company 2 fields
        if (!Company2.getText().isBlank()) {
            if (JobTitle2.getText().isBlank()) {
                showError("Job title is required for company 2");
                return false;
            }
            if (JobDescription2.getText().isBlank()) {
                showError("Job description is required for company 2");
                return false;
            }
        }

        // At least one experience entry is required
        if (Company1.getText().isBlank() && Company2.getText().isBlank()) {
            showError("At least one work experience entry is required");
            return false;
        }

        return true;
    }

    private void saveExperience() {
        JSONObject resume = ResumeService.loadResume(username);
        JSONObject experience = new JSONObject();

        experience.put("company1", Company1.getText().trim());
        experience.put("jobTitle1", JobTitle1.getText().trim());
        experience.put("jobDescription1", JobDescription1.getText().trim());
        experience.put("company2", Company2.getText().trim());
        experience.put("jobTitle2", JobTitle2.getText().trim());
        experience.put("jobDescription2", JobDescription2.getText().trim());

        resume.put("experience", experience);
        ResumeService.saveResume(username, resume);
    }

    private void navigateToLanguagesPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Languages.fxml"));
            Scene scene = new Scene(loader.load());

            LanguagesController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = (Stage) NextButton.getScene().getWindow();
            stage.setTitle("Languages");
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error navigating to languages page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}