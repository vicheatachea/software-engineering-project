package view.controllers.pages.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import util.SceneSwitcher;

public class UserProfileController {

    @FXML private ImageView profileImageView;
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private Label socialNumberLabel;

//    private User user;

    public void initialize() {
        // Load user data (this is just an example, you should load the actual user data)
//        user = new User();
//        user.setFirstName("Jane");
//        user.setLastName("Doe");
//        user.setRole("Student");
//        user.setSocialNumber("123-45-6789");

        // Set user data to UI elements
//        profileImageView.setImage(new Image("path/to/profile/image.png"));
//        nameLabel.setText(user.getFirstName() + " " + user.getLastName());
//        roleLabel.setText(user.getRole());
//        socialNumberLabel.setText(user.getSocialNumber());
    }

    @FXML
    private void handleLogout() {
        // Perform logout actions, such as clearing session data
        SceneSwitcher.switchScene((Stage) nameLabel.getScene().getWindow(), "/views/login.fxml");
    }
}