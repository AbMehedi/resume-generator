package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Personal_InfoController {

    @FXML
    private Label label;
    @FXML
    private TextField FirstName;
    @FXML
    private TextField LastName;
    @FXML
    private TextField Email;
    @FXML
    private TextField Phone;
    @FXML
    private TextField Nationality;
    @FXML
    private TextField Address;
    @FXML
    private Button SaveAndContinue;
    @FXML
    private Button Back;

    private String username;

    // Setter method to set the username passed from the previous controller (DashboardController)
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter methods for the form fields
    public String getFirstname() {
        return FirstName.getText();
    }

    public String getLastname() {
        return LastName.getText();
    }

    public String getEmail() {
        return Email.getText();
    }

    public String getPhone() {
        return Phone.getText();
    }

    public String getNationality() {
        return Nationality.getText();
    }

    public String getAddress() {
        return Address.getText();
    }

    // Go back to Dashboard
    public void backToDashBoard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader2 = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader2.load());

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public void savePersonalInfo(){
        File file = new File(username + ".txt");

       try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))){
           writer.write("Firstname : " + getFirstname());
           writer.newLine();
           writer.write("Last name : " + getLastname());
           writer.newLine();
           writer.write("Email : " + getEmail());
           writer.newLine();
           writer.write("Phone : " + getPhone());
           writer.newLine();
           writer.write("Nationality : " + getNationality());
           writer.newLine();
           writer.write("Address : " +getAddress());
           writer.newLine();
           writer.write("-------------------------------------------------/n");
       } catch(IOException e){
           e.printStackTrace();
       }
    }


    public void saveAndContinue(ActionEvent event) throws IOException {

        System.out.println("Saving personal info for user: " + username);
        System.out.println("First Name: " + getFirstname());
        System.out.println("Last Name: " + getLastname());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Nationality: " + getNationality());
        System.out.println("Address: " + getAddress());

        savePersonalInfo();


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Education.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        EducationController educationController = fxmlLoader.getController();
        educationController.setUsername(username);

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Education");
        stage.setScene(scene);
        stage.show();
    }
}
