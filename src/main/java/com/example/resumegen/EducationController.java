package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;

public class EducationController {
    @FXML private TextField CollegeName;
    @FXML private TextField College_Degree;
    @FXML private TextField College_PassingYear;
    @FXML private TextField College_CGPA;
    @FXML private TextField University_Name;
    @FXML private TextField University_Degree;
    @FXML private TextField University_PassingYear;
    @FXML private TextField University_CGPA;
    @FXML private Button SaveAndContinue;
    @FXML private Button Back;
    @FXML private Label label;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        loadEducationData();
    }

    private void loadEducationData() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("education")) {
                JSONObject edu = resume.getJSONObject("education");
                CollegeName.setText(edu.optString("collegeName", ""));
                College_Degree.setText(edu.optString("collegeDegree", ""));
                College_PassingYear.setText(edu.optString("collegePassingYear", ""));
                College_CGPA.setText(edu.optString("collegeCGPA", ""));
                University_Name.setText(edu.optString("universityName", ""));
                University_Degree.setText(edu.optString("universityDegree", ""));
                University_PassingYear.setText(edu.optString("universityPassingYear", ""));
                University_CGPA.setText(edu.optString("universityCGPA", ""));
            }
        } catch (Exception e) {
            showError("Error loading education data: " + e.getMessage());
        }
    }

    @FXML
    public void backToPersonalInfo(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Personal_Info.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Personal_InfoController controller = fxmlLoader.getController();
        controller.setUsername(username);

        Stage stage = (Stage) Back.getScene().getWindow();
        stage.setTitle("Personal Information");
        stage.setScene(scene);
    }

    @FXML
    public void SkillPage(ActionEvent event) throws IOException {
        if (validateInputs()) {
            saveEducationInfo();
            navigateToSkillsPage(event);
        }
    }

    private boolean validateInputs() {
        // College validation
        if (!CollegeName.getText().isBlank()) {
            if (College_Degree.getText().isBlank()) {
                showError("College degree is required");
                return false;
            }
            if (!isValidYear(College_PassingYear.getText())) {
                showError("Invalid college passing year");
                return false;
            }
            if (!isValidGPA(College_CGPA.getText())) {
                showError("Invalid college CGPA (0.0-4.0)");
                return false;
            }
        }

        // University validation
        if (!University_Name.getText().isBlank()) {
            if (University_Degree.getText().isBlank()) {
                showError("University degree is required");
                return false;
            }
            if (!isValidYear(University_PassingYear.getText())) {
                showError("Invalid university passing year");
                return false;
            }
            if (!isValidGPA(University_CGPA.getText())) {
                showError("Invalid university CGPA (0.0-4.0)");
                return false;
            }
        }

        return true;
    }

    private boolean isValidYear(String year) {
        if (year.isBlank()) return false;
        try {
            int yearValue = Integer.parseInt(year);
            return yearValue > 1900 && yearValue < 2100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidGPA(String gpa) {
        if (gpa.isBlank()) return false;
        try {
            double gpaValue = Double.parseDouble(gpa);
            return gpaValue >= 0.0 && gpaValue <= 4.0;
        } catch (NumberFormatException e) {
            return false;
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

    private void saveEducationInfo() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            JSONObject education = new JSONObject();

            education.put("collegeName", CollegeName.getText().trim());
            education.put("collegeDegree", College_Degree.getText().trim());
            education.put("collegePassingYear", College_PassingYear.getText().trim());
            education.put("collegeCGPA", College_CGPA.getText().trim());
            education.put("universityName", University_Name.getText().trim());
            education.put("universityDegree", University_Degree.getText().trim());
            education.put("universityPassingYear", University_PassingYear.getText().trim());
            education.put("universityCGPA", University_CGPA.getText().trim());

            resume.put("education", education);
            ResumeService.saveResume(username, resume);
        } catch (Exception e) {
            showError("Error saving education data: " + e.getMessage());
        }
    }

    private void navigateToSkillsPage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Skills.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        SkillsController skillsController = fxmlLoader.getController();
        skillsController.setUsername(username);

        Stage stage = (Stage) SaveAndContinue.getScene().getWindow();
        stage.setTitle("Skills");
        stage.setScene(scene);
    }
}