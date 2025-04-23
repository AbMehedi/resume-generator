package com.example.resumegen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;

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

    public String getUsername() {
        return Username.getText();
    }
    public String getPassword() {
        return Password.getText();
    }
    public String getEmail() {
        return Email.getText();
    }
    public String getConfirmpassword() {
        return ConfirmPassword.getText();
    }

    @FXML
    public void initialize() {
        label.setVisible(false);
    }

    public void handleSignUp(){
        if(getUsername().isEmpty() || getPassword().isEmpty() || getEmail().isEmpty()
                || getConfirmpassword().isEmpty()){
            label.setText("*Please fill all the fields");
            label.setVisible(true);
            return;

        }

        if(!getPassword().equals(getConfirmpassword())){
            label.setText("Passwords do not match");
            label.setVisible(true);
            return;
        }
        if(!getEmail().contains("@") || !getEmail().contains(".")) {
            label.setText("Email address is invalid");
            label.setVisible(true);
            return;
        }

        // if everything is valid
        label.setVisible(false);
        try {
            saveDataToFile();  // Save the user data to the file
        } catch (IOException e) {
            label.setText("Error saving user data: " + e.getMessage());
            label.setVisible(true);
        }
    }

    //save user data to file
    public void saveDataToFile() throws IOException {
        File Userfile = new File(getUsername()+".txt");
        try {
            if (!Userfile.exists()) {
                Userfile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(Userfile, true);
            BufferedWriter write = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(write);

            printWriter.println(getUsername() + "," + getPassword() + "," + getEmail());
            printWriter.close();
            write.close();
            fileWriter.close();

            label.setText("Saved");
            label.setVisible(true);
        } catch(IOException e){
            label.setText("Something went wrong");
            label.setVisible(true);
        }

    }
}


