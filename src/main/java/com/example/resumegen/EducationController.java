package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EducationController {

    @FXML
    private TextField CollegeName;
    @FXML
    private TextField College_Degree;
    @FXML
    private TextField College_PassingYear;
    @FXML
    private TextField College_CGPA;
    @FXML
    private TextField University_Name;
    @FXML
    private TextField University_Degree;
    @FXML
    private TextField University_PassingYear;
    @FXML
    private TextField University_CGPA;
    @FXML
    private Button SaveAndContinue;
    @FXML
    private Button Back;

    private String username;


    public void setUsername(String username) {
        this.username = username;
    }


    public String getCollegeName() {
        return CollegeName.getText();
    }

    public String getCollege_Degree() {
        return College_Degree.getText();
    }

    public String getCollege_PassingYear() {
        return College_PassingYear.getText();
    }

    public String getCollege_CGPA() {
        return College_CGPA.getText();
    }

    public String getUniversityName() {
        return University_Name.getText();
    }

    public String getUniversity_Degree() {
        return University_Degree.getText();
    }

    public String getUniversity_PassingYear() {
        return University_PassingYear.getText();
    }

    public String getUniversity_CGPA() {
        return University_CGPA.getText();
    }

    // Save education information to the user's file
    public void saveEducationInfo() {

        String collegeName = getCollegeName();
        String collegeDegree = getCollege_Degree();
        String collegePassingYear = getCollege_PassingYear();
        String collegeCGPA = getCollege_CGPA();
        String universityName = getUniversityName();
        String universityDegree = getUniversity_Degree();
        String universityPassingYear = getUniversity_PassingYear();
        String universityCGPA = getUniversity_CGPA();


        File file = new File(username + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {

            writer.write("College Name: " + collegeName);
            writer.newLine();
            writer.write("College Degree: " + collegeDegree);
            writer.newLine();
            writer.write("College Passing Year: " + collegePassingYear);
            writer.newLine();
            writer.write("College CGPA: " + collegeCGPA);
            writer.newLine();
            writer.write("University Name: " + universityName);
            writer.newLine();
            writer.write("University Degree: " + universityDegree);
            writer.newLine();
            writer.write("University Passing Year: " + universityPassingYear);
            writer.newLine();
            writer.write("University CGPA: " + universityCGPA);
            writer.newLine();
            writer.write("-----------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void backToPersonalInfo(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("Personal_Info.fxml"));
        Scene scene = new Scene(fxmlLoader3.load());

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Personal Info");
        stage.setScene(scene);
        stage.show();
    }


    public void SkillPage(ActionEvent event) throws IOException {

        saveEducationInfo();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Skills.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        SkillsController skillsController = fxmlLoader.getController();
        skillsController.setUsername(username);


        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Skills");
        stage.setScene(scene);
        stage.show();
    }
}
