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

    boolean found=false;

    public void LoginHandler(ActionEvent event) throws IOException {

        try{
           if( LoginCheck()){


               FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
               Scene scene = new Scene(fxmlLoader.load());

               DashboardController dashboardController = fxmlLoader.getController();
               dashboardController.setUsername(getUsername());


               Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
               stage.setTitle("Dashboard page");
               stage.setScene(scene);
               stage.show();
           }

        }catch (Exception e){
            label.setText("Login Failed 1 ");
            label.setVisible(true);
            e.printStackTrace();
        }

//
//        else{
//            label.setText("Wrong Username/Password");
//            label.setVisible(true);

       // }
       // System.out.println(LoginCheck());
    }

    public boolean LoginCheck() {
        String username = getUsername();
        String password = getPassword();


        try {
            File userFile = new File(username + ".txt");

            // If the file doesn't exist, search all .txt files
            if (!userFile.exists()) {
                File dir = new File(".");
                File[] files = dir.listFiles((directory, name) -> name.endsWith(".txt"));

                if (files != null) {
                    for (File file : files) {
                        try (Scanner filescanner = new Scanner(file)) {
                            while (filescanner.hasNextLine()) {
                                String line = filescanner.nextLine();
                                String[] parts = line.split(",");

                                if (parts.length >= 3) {
                                    String StoredUsername = parts[0].trim();
                                    String StoredPassword = parts[1].trim();
                                    String StoredMail = parts[2].trim();

                                    if (username.equals(StoredMail) && password.equals(StoredPassword)) {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (found) break;
                    }
                }
            } else {
                try (Scanner sc = new Scanner(userFile)) {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        String[] parts = line.split(",");

                        if (parts.length >= 3) {
                            String StoredUsername = parts[0].trim();
                            String StoredPassword = parts[1].trim();
                            String StoredMail = parts[2].trim();

                            if (username.equals(StoredUsername) && password.equals(StoredPassword)) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (found) {
                label.setText("Login Successful " + username);
                label.setVisible(true);
                return true;

            } else {

                label.setText("Login Failed 2");
                label.setVisible(true);
                return false;

            }

        } catch (FileNotFoundException e) {
            label.setText("Login Failed 3");
            label.setVisible(true);
            return false;

        }
    }
//    public void loginbuttonHandler(ActionEvent event) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("DashBoard.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//
//        // Get the current stage (window) from the event
//        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
//        stage.setTitle("Sign Up Page");
//        stage.setScene(scene);
//        stage.show();
//    }


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






