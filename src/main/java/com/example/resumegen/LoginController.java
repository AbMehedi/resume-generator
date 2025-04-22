package com.example.resumegen;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField Username;

    @FXML
    private PasswordField Password;

    @FXML
    private Button loginButton;

    @FXML
    private Label label;

    @FXML
    private Button button;

    public String getUsername() {

        return Username.getText();

    }

    public String getPassword() {

        return Password.getText();

    }

    public void LoginHandler() {

        if (getUsername().equals("admin") && getPassword().equals("admin")) {
            label.setText("Welcome Admin");
        }

    }


    public void SignupbuttonHandler(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sign_up_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Sign Up Page");
        stage.setScene(scene);
        stage.show();
    }



}






