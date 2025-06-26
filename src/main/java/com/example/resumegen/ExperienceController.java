package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExperienceController {
    @FXML private VBox experienceContainer;
    @FXML private Button addExperienceButton;
    @FXML private Button GenerateResume;
    @FXML private Button Back;

    private String username;
    private final List<ExperienceEntry> experienceEntries = new ArrayList<>();

    public void setUsername(String username) {
        this.username = username;
        loadExperienceData();
    }

    @FXML
    public void initialize() {
        // Add initial experience field
        addExperienceEntry(null);
    }

    @FXML
    public void addExperienceEntry(ActionEvent event) {
        addExperienceEntry("", "", "");
    }

    private void addExperienceEntry(String company, String jobTitle, String proficiency) {
        HBox experienceEntry = new HBox(10);
        experienceEntry.setPadding(new Insets(0, 0, 10, 0));

        TextField companyField = new TextField();
        companyField.setPromptText("Company");
        companyField.setPrefWidth(150);
        companyField.setText(company);

        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");
        jobTitleField.setPrefWidth(150);
        jobTitleField.setText(jobTitle);

        ComboBox<String> proficiencyCombo = new ComboBox<>();
        proficiencyCombo.setPromptText("Proficiency");
        proficiencyCombo.setPrefWidth(120);
        proficiencyCombo.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        if (proficiency != null && !proficiency.isEmpty()) {
            proficiencyCombo.setValue(proficiency);
        }

        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        removeButton.setOnAction(e -> removeExperienceEntry(experienceEntry));

        experienceEntry.getChildren().addAll(companyField, jobTitleField, proficiencyCombo, removeButton);
        experienceContainer.getChildren().add(experienceEntry);

        experienceEntries.add(new ExperienceEntry(companyField, jobTitleField, proficiencyCombo));
    }

    private void removeExperienceEntry(HBox experienceEntry) {
        ExperienceEntry entry = experienceEntries.stream()
                .filter(e -> e.container == experienceEntry)
                .findFirst()
                .orElse(null);

        if (entry != null) {
            experienceEntries.remove(entry);
            experienceContainer.getChildren().remove(experienceEntry);
        }
    }

    private void loadExperienceData() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("experience")) {
                JSONObject experience = resume.getJSONObject("experience");

                // Clear existing fields
                experienceContainer.getChildren().clear();
                experienceEntries.clear();

                // Load saved experiences
                for (int i = 1; i <= 20; i++) {
                    String companyKey = "company" + i;
                    String jobTitleKey = "jobTitle" + i;
                    String proficiencyKey = "proficiency" + i;

                    if (experience.has(companyKey)) {
                        String company = experience.getString(companyKey);
                        String jobTitle = experience.optString(jobTitleKey, "");
                        String proficiency = experience.optString(proficiencyKey, "");

                        addExperienceEntry(company, jobTitle, proficiency);
                    } else {
                        break;
                    }
                }

                // Add one empty entry if none exist
                if (experienceEntries.isEmpty()) {
                    addExperienceEntry("", "", "");
                }
            }
        } catch (Exception e) {
            showAlert("Error loading experience data: " + e.getMessage());
        }
    }

    @FXML
    public void backToSkills(ActionEvent event) throws IOException {
        // Navigation code remains the same as before
    }

    @FXML
    public void GenerateResume(ActionEvent event) {
        if (validateExperience()) {
            saveExperience();
            generatePDF();
        }
    }

    private boolean validateExperience() {
        for (ExperienceEntry entry : experienceEntries) {
            if (entry.companyField.getText().isBlank() &&
                    !entry.jobTitleField.getText().isBlank()) {
                showAlert("Company name is required for job entries");
                return false;
            }
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveExperience() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            JSONObject experience = new JSONObject();

            int entryCount = 1;
            for (ExperienceEntry entry : experienceEntries) {
                String company = entry.companyField.getText().trim();
                String jobTitle = entry.jobTitleField.getText().trim();
                String proficiency = entry.proficiencyCombo.getValue() != null ?
                        entry.proficiencyCombo.getValue().toString() : "";

                if (!company.isEmpty() || !jobTitle.isEmpty()) {
                    experience.put("company" + entryCount, company);
                    experience.put("jobTitle" + entryCount, jobTitle);
                    experience.put("proficiency" + entryCount, proficiency);
                    entryCount++;
                }
            }

            resume.put("experience", experience);
            ResumeService.saveResume(username, resume);
        } catch (Exception e) {
            showAlert("Error saving experience: " + e.getMessage());
        }
    }

    private void generatePDF() {
        // PDF generation code remains the same as before
    }

    // Helper class to manage experience entries
    private static class ExperienceEntry {
        TextField companyField;
        TextField jobTitleField;
        ComboBox<String> proficiencyCombo;
        HBox container;

        public ExperienceEntry(TextField companyField, TextField jobTitleField,
                               ComboBox<String> proficiencyCombo) {
            this.companyField = companyField;
            this.jobTitleField = jobTitleField;
            this.proficiencyCombo = proficiencyCombo;
        }
    }
}