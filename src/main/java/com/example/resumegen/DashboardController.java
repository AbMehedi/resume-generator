package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {


    private String username;

    @FXML
    private Button logOutButton;
    @FXML
    private Button createResumeButton;

    // Setter method to accept the username from LoginController
    public void setUsername(String username) {
        this.username = username;
        System.out.println("Username in DashboardController: " + this.username);  // You can remove this later
    }


    @FXML
    public void backToLogin(ActionEvent event) throws IOException {
        // Load the main login FXML
        FXMLLoader fxmlLoader3 = new FXMLLoader(Main.class.getResource("main_view.fxml"));
        Scene scene = new Scene(fxmlLoader3.load());

        // Get the current stage (window) from the event and set the new scene
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login page");
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void createResume(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Personal_Info.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Personal_InfoController personalInfoController = fxmlLoader.getController();
        personalInfoController.setUsername(username);

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Personal Information");
        stage.setScene(scene);
        stage.show();
    }
}
