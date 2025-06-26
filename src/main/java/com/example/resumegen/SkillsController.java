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
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SkillsController {
    @FXML private VBox skillsContainer;
    @FXML private Button addSkillButton;
    @FXML private Button SaveAndContinue;
    @FXML private Button Back;
    @FXML private Label label;

    private String username;
    private final List<SkillRow> skillRows = new ArrayList<>();

    // Skill row inner class to manage UI components
    private static class SkillRow {
        TextField nameField;
        TextField percentField;
        HBox container;
    }

    public void setUsername(String username) {
        this.username = username;
        loadSkillsData();
    }

    @FXML
    public void initialize() {
        // Add initial skill field
        addSkillField("", "");
    }

    @FXML
    public void addSkillField(ActionEvent event) {
        addSkillField("", "");
    }

    private void addSkillField(String name, String percent) {
        SkillRow row = new SkillRow();
        HBox skillEntry = new HBox(10);
        skillEntry.setPadding(new Insets(0, 0, 10, 0));

        // Skill name field with autocomplete
        row.nameField = new TextField();
        row.nameField.setPromptText("Skill name");
        row.nameField.setPrefWidth(200);
        row.nameField.setText(name);

        // Add auto-capitalization formatter
        row.nameField.setTextFormatter(new TextFormatter<>(change -> {
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
        TextFields.bindAutoCompletion(row.nameField,
                "Java", "Python", "JavaScript", "C++", "C#", "SQL",
                "HTML", "CSS", "React", "Angular", "Vue.js",
                "Node.js", "Spring Boot", "Django", "Flask",
                "Project Management", "Team Leadership", "Communication",
                "Problem Solving", "Agile Methodologies", "Git"
        );

        // Skill percentage field with validation (0-100)
        row.percentField = new TextField();
        row.percentField.setPromptText("Percentage (0-100)");
        row.percentField.setPrefWidth(100);
        row.percentField.setText(percent);

        // Add numeric validation
        Pattern pattern = Pattern.compile("\\d*");
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                if (!change.getControlNewText().isEmpty()) {
                    int value = Integer.parseInt(change.getControlNewText());
                    if (value > 100) {
                        change.setText("100");
                        change.setRange(0, change.getControlText().length());
                    }
                }
                return change;
            }
            return null;
        });
        row.percentField.setTextFormatter(formatter);

        // Remove button
        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        removeButton.setOnAction(e -> removeSkillRow(row));

        skillEntry.getChildren().addAll(row.nameField, row.percentField, removeButton);
        skillsContainer.getChildren().add(skillEntry);

        row.container = skillEntry;
        skillRows.add(row);
    }

    private void removeSkillRow(SkillRow row) {
        skillsContainer.getChildren().remove(row.container);
        skillRows.remove(row);
    }

    private void loadSkillsData() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("skills")) {
                JSONArray skillsArray = resume.getJSONArray("skills");

                // Clear existing fields
                skillsContainer.getChildren().clear();
                skillRows.clear();

                // Load saved skills
                for (int i = 0; i < skillsArray.length(); i++) {
                    JSONObject skill = skillsArray.getJSONObject(i);
                    addSkillField(
                            skill.optString("name", ""),
                            skill.optString("percent", "")
                    );
                }

                // Add one empty field if none exist
                if (skillRows.isEmpty()) {
                    addSkillField("", "");
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
        boolean hasValidSkill = false;

        for (SkillRow row : skillRows) {
            String name = row.nameField.getText().trim();
            String percent = row.percentField.getText().trim();

            if (!name.isEmpty() && !percent.isEmpty()) {
                hasValidSkill = true;
            } else if (!name.isEmpty() && percent.isEmpty()) {
                showError("Please enter percentage for: " + name);
                return false;
            } else if (name.isEmpty() && !percent.isEmpty()) {
                showError("Please enter skill name for percentage");
                return false;
            }
        }

        if (!hasValidSkill) {
            showError("At least one skill with percentage is required");
            return false;
        }
        return true;
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
            JSONArray skillsArray = new JSONArray();

            for (SkillRow row : skillRows) {
                String name = row.nameField.getText().trim();
                String percent = row.percentField.getText().trim();

                if (!name.isEmpty() && !percent.isEmpty()) {
                    JSONObject skill = new JSONObject();
                    skill.put("name", name);
                    skill.put("percent", percent);
                    skillsArray.put(skill);
                }
            }

            resume.put("skills", skillsArray);
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