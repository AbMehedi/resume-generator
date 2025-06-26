package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkillsController {
    @FXML private VBox skillsContainer;
    @FXML private Button addSkillButton;
    @FXML private Button SaveAndContinue;
    @FXML private Button Back;
    @FXML private Label label;

    private String username;
    private final List<TextField> skillFields = new ArrayList<>();
    private static final String[] SKILL_SUGGESTIONS = {
            "Java", "Python", "JavaScript", "C++", "C#", "SQL",
            "HTML", "CSS", "React", "Angular", "Vue.js",
            "Node.js", "Spring Boot", "Django", "Flask",
            "Project Management", "Team Leadership", "Communication",
            "Problem Solving", "Agile Methodologies", "Git"
    };

    public void setUsername(String username) {
        this.username = username;
        loadSkillsData();
    }

    @FXML
    public void initialize() {
        // Add initial skill field
        addSkillField((ActionEvent) null);
    }

    @FXML
    public void addSkillField(ActionEvent event) {
        addSkillField("");
    }

    private void addSkillField(String initialValue) {
        HBox skillEntry = new HBox(10);
        skillEntry.setPadding(new Insets(0, 0, 10, 0));

        TextField skillField = new TextField();
        skillField.setPromptText("Enter skill");
        skillField.setPrefWidth(300);
        skillField.setText(initialValue);

        // Add autocomplete
        TextFields.bindAutoCompletion(skillField, SKILL_SUGGESTIONS);

        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        removeButton.setOnAction(e -> removeSkillField(skillEntry));

        skillEntry.getChildren().addAll(skillField, removeButton);
        skillsContainer.getChildren().add(skillEntry);
        skillFields.add(skillField);
    }

    private void removeSkillField(HBox skillEntry) {
        TextField field = (TextField) skillEntry.getChildren().get(0);
        skillFields.remove(field);
        skillsContainer.getChildren().remove(skillEntry);
    }

    private void loadSkillsData() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("skills")) {
                JSONObject skills = resume.getJSONObject("skills");

                // Clear existing fields
                skillsContainer.getChildren().clear();
                skillFields.clear();

                // Load saved skills
                for (int i = 1; i <= 20; i++) {
                    String skillKey = "skill" + i;
                    if (skills.has(skillKey)) {
                        String skillValue = skills.getString(skillKey);
                        if (!skillValue.isBlank()) {
                            addSkillField(skillValue);
                        }
                    } else {
                        break;
                    }
                }

                // Add one empty field if none exist
                if (skillFields.isEmpty()) {
                    addSkillField("");
                }
            }
        } catch (Exception e) {
            showError("Error loading skills data: " + e.getMessage());
        }
    }

    @FXML
    public void backToEducationPage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Education.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        EducationController controller = fxmlLoader.getController();
        controller.setUsername(username);

        Stage stage = (Stage) Back.getScene().getWindow();
        stage.setTitle("Education");
        stage.setScene(scene);
    }

    @FXML
    public void Experience(ActionEvent event) throws IOException {
        if (validateSkills()) {
            saveSkills();
            navigateToExperiencePage(event);
        }
    }

    private boolean validateSkills() {
        // Check if at least one skill is provided
        for (TextField field : skillFields) {
            if (!field.getText().isBlank()) {
                return true;
            }
        }
        showError("At least one skill is required");
        return false;
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

    private void saveSkills() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            JSONObject skills = new JSONObject();

            int skillCount = 1;
            for (TextField field : skillFields) {
                String skill = field.getText().trim();
                if (!skill.isEmpty()) {
                    skills.put("skill" + skillCount, skill);
                    skillCount++;
                }
            }

            resume.put("skills", skills);
            ResumeService.saveResume(username, resume);
        } catch (Exception e) {
            showError("Error saving skills: " + e.getMessage());
        }
    }

    private void navigateToExperiencePage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Experience.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        ExperienceController controller = fxmlLoader.getController();
        controller.setUsername(username);

        Stage stage = (Stage) SaveAndContinue.getScene().getWindow();
        stage.setTitle("Work Experience");
        stage.setScene(scene);
    }
}