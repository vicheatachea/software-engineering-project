package view.controllers.pages.user;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class UserProfileController {
    private UserController userController;
    private Stage stage;

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label socialNumberLabel;

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogout() {
        userController.setUserLoggedIn(false);
        stage.close();
    }

    @FXML
    public void initialize() {
        nameLabel.setText(userController.getUserModel().getFirstName() + " " + userController.getUserModel().getLastName());
        roleLabel.setText(userController.getUserModel().getRole());
        socialNumberLabel.setText(userController.getUserModel().getSocialNumber());
    }
}