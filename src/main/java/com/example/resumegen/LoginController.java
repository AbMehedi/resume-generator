package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML
    private TextField Username;

    @FXML
    private TextField Password;

    @FXML
    private Button loginButton;

    @FXML
    private Label label;

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






}