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

public class SignUpController {
    @FXML
    private TextField Username;
    @FXML
    private PasswordField Password;
    @FXML
    private TextField Email;
    @FXML
    private PasswordField ConfirmPassword;
    @FXML
    private Label label;
    @FXML
    private Button SignUp;
    @FXML
    private Button Login;

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

    public String getEmail() {
        return Email.getText().trim();
    }

    public String getConfirmpassword() {
        return ConfirmPassword.getText().trim();
    }

    @FXML
    public void handleSignUp() {
        // Reset error state
        label.setVisible(false);

        // Validate input fields
        if (getUsername().isEmpty() || getPassword().isEmpty() ||
                getEmail().isEmpty() || getConfirmpassword().isEmpty()) {
            showError("* Please fill all required fields");
            return;
        }

        if (!getPassword().equals(getConfirmpassword())) {
            showError("Passwords do not match");
            return;
        }
        if(getPassword().length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!isValidEmail(getEmail())) {
            showError("Invalid email format (must contain '@' and '.')");
            return;
        }

        // Attempt to create user
        if (UserService.createUser(getUsername(), getEmail(), getPassword())) {
            showSuccess("Registration successful! Redirecting to login...");

            // Auto-redirect to login after short delay
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                try {
                                    backToLoginPage(null);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    },
                    2000  // 2 second delay
            );
        } else {
            showError("Username or email already exists");
        }
    }

    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.contains("@") && email.contains(".");
    }

    private void showError(String message) {
        label.setStyle("-fx-text-fill: red;");
        label.setText(message);
        label.setVisible(true);
    }

    private void showSuccess(String message) {
        label.setStyle("-fx-text-fill: green;");
        label.setText(message);
        label.setVisible(true);
    }

    @FXML
    public void backToLoginPage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Get current stage
        Stage stage;
        if (event != null) {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        } else {
            // Handle auto-redirect case
            stage = (Stage) Login.getScene().getWindow();
        }

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}