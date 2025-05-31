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

public class ExperienceController {

    private String username;

    public void getusername(String username) {
        this.username = username;
    }

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


    public void saveExperienece(){
        String company1 = Company1.getText();
        String jobTitle1 = JobTitle1.getText();
        String company2 = Company2.getText();
        String jobTitle2 = JobTitle2.getText();

        File file = new File(username +"txt");

        try (BufferedWriter writer =new BufferedWriter(new FileWriter(file,true))) {
            writer.write("Company 1 "+ company1);
            writer.newLine();
            writer.write("JobTitle1 "+ jobTitle1);
            writer.newLine();
            writer.write("Company2 "+ company2);
            writer.newLine();
            writer.write("JobTitle2 "+ jobTitle2);
            writer.newLine();
            writer.write("-----------------------------------");
            writer.newLine();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

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
        saveExperienece();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sign_up_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login page");
        stage.setScene(scene);
        stage.show();
    }
}
