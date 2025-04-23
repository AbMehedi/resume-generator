package com.example.resumegen;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

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

    public void initialize() {
        label.setVisible(false);
    }

    public void LoginHandler() {
//
//        if (getUsername().equals("admin") && getPassword().equals("admin")) {
//            label.setText("Welcome Admin");
//        }

        try{
            LoginCheck();
        }catch (Exception e){
            label.setText("Login Failed ");
            label.setVisible(true);
        }

    }

    public void LoginCheck() {
        boolean found = false;
        String username = getUsername();
        //System.out.println(username);
        String password = getPassword();
        try{
            File userFile = new File(username+".txt");
            if(!userFile.exists()) {
                File dir = new File(".");
                File[] files = dir.listFiles((directory, name) -> name.endsWith(".txt"));
                if (files != null) {
                    for (File file : files) {
                        Scanner filescanner = new Scanner(file);

                        while (filescanner.hasNextLine()) {
                            String line = filescanner.nextLine();
                            String[] parts = line.split(",");

                            if (parts.length >= 3) {
                                String StoredUsername = parts[0];
                                String StoredPassword = parts[1];
                                String StoredMail = parts[2];
                                System.out.println(StoredMail);

                                if (username.equals(StoredMail) && password.equals(StoredPassword)) {
                                     found = true;
                                    break;
                                }
                            }
                        }
                    }
                }

            }
            Scanner sc = new Scanner(userFile);
            found = false;
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] parts = line.split(",");

                if(parts.length>=3){
                    String StoredUsername = parts[0];
                    String StoredPassword = parts[1];
                    String StoredMail = parts[2];
                    System.out.println(StoredMail);

                    if(username.equals(StoredUsername) && password.equals(StoredPassword)){
                        found = true;
                        break;
                    }

                }

            }
            //check if login is successfull
            if (found){
                label.setText("Login Successful "+username);
                label.setVisible(true);
            } else {
                label.setText("Login Failed");
                label.setVisible(true);
            }

        }catch(FileNotFoundException e){
            label.setText("Login Failed");
            label.setVisible(true);
        }
    }

    //scene change
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






