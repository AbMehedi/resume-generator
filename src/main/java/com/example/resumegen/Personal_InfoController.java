package com.example.resumegen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class Personal_InfoController {
    @FXML private Label label;
    @FXML private TextField FirstName;
    @FXML private TextField LastName;
    @FXML private TextField Email;
    @FXML private TextField Phone;
    @FXML private TextField Nationality;
    @FXML private TextField Address;
    @FXML private TextArea AboutMe;
    @FXML private Button SaveAndContinue;
    @FXML private Button Back;
    @FXML private Button chooseImageButton;
    @FXML private ImageView profileImageView;
    @FXML private Label imageErrorLabel;

    private String username;
    private String profilePictureBase64;

    public void setUsername(String username) {
        this.username = username;
        loadPersonalInfo();
    }

    @FXML
    public void initialize() {
        // Add auto-capitalization to single-line text fields
        configureCapitalization(FirstName);
        configureCapitalization(LastName);
        configureCapitalization(Nationality);
        configureCapitalization(Address);

        // Add sentence capitalization to AboutMe text area
        configureSentenceCapitalization(AboutMe);
    }

    private void configureCapitalization(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isAdded() || change.isReplaced()) {
                int start = change.getRangeStart();
                if (start == 0) {
                    // Capitalize first character at position 0
                    String newText = change.getText();
                    if (!newText.isEmpty()) {
                        change.setText(newText.substring(0, 1).toUpperCase() +
                                (newText.length() > 1 ? newText.substring(1) : ""));
                    }
                }
            }
            return change;
        }));
    }

    // Configure auto-capitalization for sentences
    private void configureSentenceCapitalization(TextArea area) {
        area.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isAdded()) {
                String text = change.getText();
                String prevText = change.getControlText();
                int start = change.getRangeStart();

                // Capitalize if:
                // 1. At start of text
                // 2. After sentence-ending punctuation
                if (start == 0 ||
                        (start >= 2 &&
                                Pattern.compile("[.!?]\\s+$").matcher(prevText.substring(0, start)).find())) {
                    if (!text.isEmpty()) {
                        change.setText(text.substring(0, 1).toUpperCase() +
                                (text.length() > 1 ? text.substring(1) : ""));
                    }
                }
            }
            return change;
        }));
    }
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // Calculate new dimensions while maintaining aspect ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double aspectRatio = (double) originalWidth / originalHeight;

        int newWidth = targetWidth;
        int newHeight = targetHeight;

        if (originalWidth > originalHeight) {
            newHeight = (int) (targetWidth / aspectRatio);
        } else {
            newWidth = (int) (targetHeight * aspectRatio);
        }

        // Create new image with calculated dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        // Configure rendering quality
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the resized image
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }


    private void loadPersonalInfo() {
        try {
            JSONObject resume = ResumeService.loadResume(username);
            if (resume.has("personal")) {
                JSONObject personal = resume.getJSONObject("personal");
                // Existing fields
                FirstName.setText(personal.optString("firstName", ""));
                LastName.setText(personal.optString("lastName", ""));
                Email.setText(personal.optString("email", ""));
                Phone.setText(personal.optString("phone", ""));
                Nationality.setText(personal.optString("nationality", ""));
                Address.setText(personal.optString("address", ""));
                AboutMe.setText(personal.optString("aboutMe", ""));

                // Load profile picture
                if (personal.has("profilePicture")) {
                    profilePictureBase64 = personal.getString("profilePicture");
                    profileImageView.setImage(convertBase64ToImage(profilePictureBase64));
                }
            }
        } catch (Exception e) {
            showError("Error loading personal data: " + e.getMessage());
        }
    }

    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ImageFiles", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Validate file size (max 2MB)
                if (selectedFile.length() > 2 * 1024 * 1024) {
                    imageErrorLabel.setText("Image too large (max 2MB)");
                    imageErrorLabel.setVisible(true);
                    return;
                }

                // Process and display image
                profilePictureBase64 = convertImageToBase64(selectedFile);
                profileImageView.setImage(convertBase64ToImage(profilePictureBase64));
                imageErrorLabel.setVisible(false);
            } catch (IOException e) {
                imageErrorLabel.setText("Error loading image");
                imageErrorLabel.setVisible(true);
            }
        }
    }

    private String convertImageToBase64(File file) throws IOException {
        try (FileInputStream imageStream = new FileInputStream(file)) {
            // Read original image
            BufferedImage original = ImageIO.read(imageStream);
            if (original == null) {
                throw new IOException("Unsupported image format");
            }

            // Resize image to max 150x150
            BufferedImage resized = resizeImage(original, 400, 400);

            // Convert to JPEG with compression
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpg", byteStream);

            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        }
    }

    private Image convertBase64ToImage(String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        byte[] imageData = Base64.getDecoder().decode(base64);
        return new Image(new java.io.ByteArrayInputStream(imageData));
    }

    // Validate required fields
    private boolean validateInputs() {
        // Reset error states
        label.setVisible(false);
        imageErrorLabel.setVisible(false);

        // Validate name fields
        if (FirstName.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }
        if (LastName.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }

        // Validate email
        if (Email.getText().trim().isEmpty()) {
            showError("Email is required");
            return false;
        }
        if (!isValidEmail(Email.getText().trim())) {
            showError("Invalid email format");
            return false;
        }

        // Validate about me
        if (AboutMe.getText().trim().length() < 30) {
            showError("About Me should be at least 30 characters");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // More comprehensive email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void showError(String message) {
        label.setText(message);
        label.setVisible(true);
    }

    @FXML
    public void backToDashBoard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        DashboardController dashboardController = fxmlLoader.getController();
        dashboardController.setUsername(username);

        Stage stage = (Stage) Back.getScene().getWindow();
        stage.setTitle("Dashboard");
        stage.setScene(scene);
    }

    @FXML
    public void saveAndContinue(ActionEvent event) throws IOException {
        // Hide any previous errors
        label.setVisible(false);
        imageErrorLabel.setVisible(false);

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Save data to JSON
        savePersonalInfo();

        // Navigate to Education screen
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Education.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        EducationController educationController = fxmlLoader.getController();
        educationController.setUsername(username);

        Stage stage = (Stage) SaveAndContinue.getScene().getWindow();
        stage.setTitle("Education");
        stage.setScene(scene);
    }

    private void savePersonalInfo() {
        try {
            // Get the existing resume data
            JSONObject resume = ResumeService.loadResume(username);

            // Create personal info JSON object
            JSONObject personal = new JSONObject();
            personal.put("firstName", FirstName.getText().trim());
            personal.put("lastName", LastName.getText().trim());
            personal.put("email", Email.getText().trim());
            personal.put("phone", Phone.getText().trim());
            personal.put("nationality", Nationality.getText().trim());
            personal.put("address", Address.getText().trim());
            personal.put("aboutMe", AboutMe.getText().trim());

            // Add profile picture if exists
            if (profilePictureBase64 != null && !profilePictureBase64.isEmpty()) {
                personal.put("profilePicture", profilePictureBase64);
            }

            // Add personal info to resume
            resume.put("personal", personal);

            // Save to file
            ResumeService.saveResume(username, resume);
        } catch (Exception e) {
            showError("Error saving personal data: " + e.getMessage());
        }
    }
}