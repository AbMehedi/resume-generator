// InterestsController.java
package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Desktop;
import com.itextpdf.html2pdf.HtmlConverter;

public class InterestsController {
    @FXML private ListView<String> interestsList;
    @FXML private TextField interestField;
    @FXML private Button addButton;
    @FXML private Button backButton;
    @FXML private Button generateButton;
    @FXML private Label errorLabel;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        loadInterests();
    }

    private void loadInterests() {
        try {
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
        generatePDF();
    }

    private void generatePDF() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            String html = TemplateEngine.generateResume(resume);

            String pdfPath = username + "_resume.pdf";
            HtmlConverter.convertToPdf(html, new FileOutputStream(pdfPath));

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(pdfPath));
            }

            showSuccessAlert("Resume Generated", "PDF saved to: " + pdfPath);
        } catch (Exception e) {
            showError("PDF Generation Failed: " + e.getMessage());
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}