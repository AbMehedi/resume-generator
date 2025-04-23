package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


import java.io.IOException;

public class DashboardController {
    @FXML
    private Button logOutButton;
    @FXML
    private Button createResumeButton;
    @FXML
    private void backToLogin(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login page");
        stage.setScene(scene);
        stage.show();

    }

    public void createResume(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Personal_Info.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get the current stage (window) from the event
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setTitle("Login page");
        stage.setScene(scene);
        stage.show();
    }
}
