package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ExperienceController {

    @FXML
    private TextField Company1;
    @FXML
    private TextField JobTitle1;
    @FXML
    private TextField Company2;
    @FXML
    private TextField JobTitle2;
    @FXML
    private Button GenerateResume;
    @FXML
    private Button Back;

    public void backToSkills(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("Skills.fxml"));
        Scene scene = new Scene(fxmlLoader3.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Skills");
        stage.setScene(scene);
        stage.show();

    }

    public void SkillPage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sign_up_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login page");
        stage.setScene(scene);
        stage.show();
    }
}
