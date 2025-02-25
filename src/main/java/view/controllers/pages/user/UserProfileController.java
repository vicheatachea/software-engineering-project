package view.controllers.pages.user;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class UserProfileController {
    private Controller controller;

    @FXML private ImageView profileImageView;
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private Label socialNumberLabel;

    public void setController(Controller controller) {
        this.controller = controller;
        loadUserData();
    }

    private void loadUserData() {
        // Mock user data for demonstration purposes
        String firstName = "Jane";
        String lastName = "Doe";
        String role = "Student";
        String socialNumber = "123-45-6789";
        String profileImagePath = "path/to/profile/image.png"; // Replace with actual path

        // Set user data to UI elements
        profileImageView.setImage(new Image(profileImagePath));
        nameLabel.setText(firstName + " " + lastName);
        roleLabel.setText(role);
        socialNumberLabel.setText(socialNumber);
    }

    @FXML
    private void handleLogout() {
        controller.setUserLoggedIn(false);
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}