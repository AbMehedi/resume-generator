package com.example.resumegen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class DashBoardController {

    @FXML
    private Label label;

    @FXML
    private Button LogOut;

    @FXML
    private Button CreateNewResume;

    @FXML
    private void initialize() {
        label.setText("Welcome to your dashboard! ✨");
    }

    @FXML
    private void handleCreateResume() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Personal_info.fxml"));
        Stage stage = (Stage) CreateNewResume.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleLogOut() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main_view.fxml"));
        Stage stage = (Stage) LogOut.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
