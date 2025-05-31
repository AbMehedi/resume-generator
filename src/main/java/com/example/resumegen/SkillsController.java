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

public class SkillsController {

    private String username;

    @FXML
    private TextField Skill1;
    @FXML
    private TextField Skill2;
    @FXML
    private TextField Skill3;
    @FXML
    private TextField Skill4;
    @FXML
    private Button SaveAndContinue;
    @FXML
    private Button Back;

    public void setUsername (String username){
        this.username = username;
    }

    public void saveSkill(){
        String skill1 = Skill1.getText();
        String skill2 = Skill2.getText();
        String skill3 = Skill3.getText();
        String skill4 = Skill4.getText();

        File file = new File(username + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,true))){
            writer.write("Skill 1 : " +skill1);

            writer.newLine();
            writer.write(("Skill 2 : " +skill2));
            writer.newLine();
            writer.write(("Skill 3 : " +skill3));
            writer.newLine();
            writer.write(("Skill 4 : " +skill4));
            writer.newLine();
            writer.flush();

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void backToEducationPage(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("Education.fxml"));
        Scene scene = new Scene(fxmlLoader3.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login page");
        stage.setScene(scene);
        stage.show();

    }

    public void Experience(ActionEvent event) throws IOException {

        saveSkill();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Experience.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        ExperienceController controller = (ExperienceController) fxmlLoader.getController();
        controller.getusername(username);

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login page");
        stage.setScene(scene);
        stage.show();
    }
}
