package com.example.resumegen;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.concurrent.Task;

public class InterestsController {
    @FXML private ListView<String> interestsList;
    @FXML private TextField interestField;
    @FXML private Button addButton;
    @FXML private Button backButton;
    @FXML private Button generateButton;
    @FXML private Label errorLabel;

    private String username;

    @FXML
    public void initialize() {
        // Add auto-capitalization to interest field
        interestField.setTextFormatter(new TextFormatter<>(change -> {
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

        TextFields.bindAutoCompletion(interestField,
                "Reading", "Writing", "Traveling", "Photography", "Cooking",
                "Gardening", "Painting", "Drawing", "Music", "Dancing",
                "Singing", "Hiking", "Cycling", "Swimming", "Running",
                "Yoga", "Meditation", "Chess", "Board Games", "Video Games",
                "Movies", "Theater", "Blogging", "Podcasting", "Volunteering",
                "Learning languages", "Programming", "DIY Projects", "Woodworking",
                "Knitting", "Fishing", "Camping", "Skiing", "Surfing", "Diving",
                "Astronomy", "Bird watching", "Collecting", "Wine tasting", "Baking"
        );
    }

    public void setUsername(String username) {
        this.username = username;
        loadInterests();
    }

    private void loadInterests() {
        try {
            interestsList.getItems().clear();
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("interests")) {
                JSONArray interests = resume.getJSONArray("interests");
                for (int i = 0; i < interests.length(); i++) {
                    interestsList.getItems().add(interests.getString(i));
                }
            }
        } catch (Exception e) {
            showError("Error loading interests: " + e.getMessage());
        }
    }

    @FXML
    public void addInterest() {
        String interest = interestField.getText().trim();
        if (interest.isEmpty()) {
            showError("Please enter an interest");
            return;
        }
        interestsList.getItems().add(interest);
        interestField.clear();
        errorLabel.setVisible(false);
    }

    @FXML
    public void removeInterest() {
        int selectedIndex = interestsList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            interestsList.getItems().remove(selectedIndex);
        }
    }

    private void saveInterests() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            JSONArray interests = new JSONArray();
            for (String interest : interestsList.getItems()) {
                interests.put(interest);
            }
            resume.put("interests", interests);
            ResumeService.saveResume(username, resume);
        } catch (Exception e) {
            showError("Error saving interests: " + e.getMessage());
        }
    }

    @FXML
    public void backToLanguages(ActionEvent event) throws IOException {
        saveInterests();
        navigateToLanguagesPage(event);
    }

    private void navigateToLanguagesPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Languages.fxml"));
        Scene scene = new Scene(loader.load());
        LanguagesController controller = loader.getController();
        controller.setUsername(username);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setTitle("Languages");
        stage.setScene(scene);
    }

    @FXML
    public void generateResume() {
        saveInterests();
        generateButton.setDisable(true);

        Task<Void> generationTask = new Task<>() {
            private ResumeGenerator resumeGenerator;

            @Override
            protected Void call() throws Exception {
                resumeGenerator = new ResumeGenerator(username);
                // Generate and open PDF first
                resumeGenerator.generateAndOpen();
                return null;
            }

            @Override
            protected void succeeded() {
                // Send email in background after PDF is opened
                new Thread(() -> {
                    resumeGenerator.sendEmail();
                    Platform.runLater(() -> {
                        String successMessage = "HTML version saved to: " + resumeGenerator.getHtmlPath() + "\n" +
                                "PDF version saved to: " + resumeGenerator.getPdfPath();

                        if (!resumeGenerator.getUserEmail().isEmpty()) {
                            successMessage += "\nEmail sent to: " + resumeGenerator.getUserEmail();
                        }

                        showSuccessAlert("Resume Generated", successMessage);
                        generateButton.setDisable(false);
                    });
                }).start();
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    generateButton.setDisable(false);
                    showError("Resume Generation Failed: " + getException().getMessage());
                });
            }
        };

        new Thread(generationTask).start();
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        });
    }

    private void showSuccessAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}