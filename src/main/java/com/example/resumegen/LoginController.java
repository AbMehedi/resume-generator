package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    @FXML
    public void initialize() {
        // Hide the error label initially
        label.setVisible(false);
    }

    public String getUsername() {
        return Username.getText().trim();
    }

    public String getPassword() {
        return Password.getText().trim();
    }

    @FXML
    public void LoginHandler(ActionEvent event) {
        // Reset error state
        label.setVisible(false);

        // Validate input fields
        if (getUsername().isEmpty() || getPassword().isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            // Attempt authentication
            if (UserService.validateUser(getUsername(), getPassword())) {
                // Successful login
                navigateToDashboard(event);
            } else {
                // Authentication failed
                showError("Invalid username or password");
            }
        } catch (Exception e) {
            // Handle unexpected errors
            showError("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            // Load the dashboard view
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Pass username to the dashboard controller
            DashboardController dashboardController = fxmlLoader.getController();
            dashboardController.setUsername(getUsername());

            // Switch to the dashboard scene
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Dashboard");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void SignupbuttonHandler(ActionEvent event) throws IOException {
        // Load the signup view
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sign_up_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Switch to the signup scene
        Stage stage = (Stage) button.getScene().getWindow();
        stage.setTitle("Sign Up");
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void showError(String message) {
        label.setStyle("-fx-text-fill: red;");
        label.setText(message);
        label.setVisible(true);
    }
}