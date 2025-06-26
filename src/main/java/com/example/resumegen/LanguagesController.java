package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Desktop;
import com.itextpdf.html2pdf.HtmlConverter;

public class LanguagesController {
    @FXML private ListView<String> languagesList;
    @FXML private TextField languageField;
    @FXML private ComboBox<String> proficiencyComboBox;
    @FXML private Button addButton;
    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private Label errorLabel;

    private String username;

    @FXML
    public void initialize() {
        // Add auto-capitalization to language field
        languageField.setTextFormatter(new TextFormatter<>(change -> {
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

        TextFields.bindAutoCompletion(languageField,
                "English", "Spanish", "French", "German", "Chinese",
                "Hindi", "Arabic", "Portuguese", "Russian", "Japanese",
                "Italian", "Korean", "Dutch", "Turkish", "Swedish",
                "Polish", "Vietnamese", "Thai", "Indonesian", "Urdu",
                "Bengali", "Punjabi", "Malay", "Filipino", "Greek",
                "Czech", "Hungarian", "Romanian", "Danish", "Finnish",
                "Norwegian", "Hebrew", "Ukrainian", "Catalan", "Croatian"
        );
    }

    public void setUsername(String username) {
        this.username = username;
        initializeComboBox();
        loadLanguages();
    }

    private void initializeComboBox() {
        proficiencyComboBox.getItems().addAll(
                "Native", "Fluent", "Intermediate", "Basic"
        );
        proficiencyComboBox.setValue("Intermediate");
    }

    private void loadLanguages() {
        try {
            languagesList.getItems().clear();
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("languages")) {
                JSONArray languages = resume.getJSONArray("languages");
                for (int i = 0; i < languages.length(); i++) {
                    JSONObject lang = languages.getJSONObject(i);
                    languagesList.getItems().add(
                            lang.getString("name") + " - " + lang.getString("proficiency")
                    );
                }
            }
        } catch (Exception e) {
            showError("Error loading languages: " + e.getMessage());
        }
    }

    @FXML
    public void addLanguage() {
        String language = languageField.getText().trim();
        String proficiency = proficiencyComboBox.getValue();

        if (language.isEmpty()) {
            showError("Please enter a language");
            return;
        }

        if (proficiency == null) {
            showError("Please select proficiency level");
            return;
        }

        // Add to list
        languagesList.getItems().add(language + " - " + proficiency);
        languageField.clear();
        errorLabel.setVisible(false);
    }

    @FXML
    public void removeLanguage() {
        int selectedIndex = languagesList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            languagesList.getItems().remove(selectedIndex);
        }
    }

    private void saveLanguages() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            JSONArray languages = new JSONArray();

            for (String item : languagesList.getItems()) {
                String[] parts = item.split(" - ");
                if (parts.length == 2) {
                    JSONObject lang = new JSONObject();
                    lang.put("name", parts[0]);
                    lang.put("proficiency", parts[1]);
                    languages.put(lang);
                }
            }

            resume.put("languages", languages);
            ResumeService.saveResume(username, resume);
        } catch (Exception e) {
            showError("Error saving languages: " + e.getMessage());
        }
    }

    @FXML
    public void backToExperience(ActionEvent event) throws IOException {
        saveLanguages();
        navigateToExperiencePage(event);
    }

    private void navigateToExperiencePage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Experience.fxml"));
        Scene scene = new Scene(loader.load());

        ExperienceController controller = loader.getController();
        controller.setUsername(username);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setTitle("Work Experience");
        stage.setScene(scene);
    }

    @FXML
    public void goToInterests(ActionEvent event) {
        if (validateLanguages()) {
            saveLanguages();
            navigateToInterestsPage(event);
        }
    }

    private boolean validateLanguages() {
        if (languagesList.getItems().isEmpty()) {
            showError("Please add at least one language");
            return false;
        }
        return true;
    }

    private void navigateToInterestsPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Interests.fxml"));
            Scene scene = new Scene(loader.load());

            InterestsController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setTitle("Interests");
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error navigating to interests page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}