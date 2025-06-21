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

    // This method is used to receive the username from the previous screen
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

    // Save experience information to the user's file
    public void saveExperience() {
        String company1 = Company1.getText();
        String jobTitle1 = JobTitle1.getText();
        String company2 = Company2.getText();
        String jobTitle2 = JobTitle2.getText();

        File file = new File(username + ".txt"); // ✅ fixed missing dot in file extension

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("Experience");
            writer.newLine();
            writer.write("Company 1 : " + company1);
            writer.newLine();
            writer.write("Job Title 1 : " + jobTitle1);
            writer.newLine();
            writer.write("Company 2 : " + company2);
            writer.newLine();
            writer.write("Job Title 2 : " + jobTitle2);
            writer.newLine();
            writer.write("-----------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigate back to Skills screen
    public void backToSkills(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Skills.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        SkillsController controller = fxmlLoader.getController();
        controller.setUsername(username); // Pass username back

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Skills");
        stage.setScene(scene);
        stage.show();
    }

    // Generate PDF after saving experience details
    public void GenerateResume(ActionEvent event) {
        saveExperience(); // Save the last section
        PdfGenerator.generateFromFile(username); // Generate PDF with all collected data

        System.out.println("✅ Resume PDF generated successfully for user: " + username);
        // You can add an alert or screen redirect here
    }
}
