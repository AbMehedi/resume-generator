package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EducationController {

    @FXML
    private TextField CollegeName;
    @FXML
    private TextField Degree;
    @FXML
    private TextField PassingYear;
    @FXML
    private TextField CGPA;
    @FXML
    private Button SaveAndContinue;
    @FXML
    private Button Back;


    public void backToPersonalInfo(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("Personal_Info.fxml"));
        Scene scene = new Scene(fxmlLoader3.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Personal Info");
        stage.setScene(scene);
        stage.show();

    }

    public void SkillPage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Skills.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Skills");
        stage.setScene(scene);
        stage.show();
    }
}
