package view.controllers.pages.user;

import controller.UserController;
import dto.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private UserController userController;
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (userController.authenticateUser(username, password)) {
            userController.setUserLoggedIn(true);
            stage.close(); // Close the login popup
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    @FXML
    private void goToRegisterPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/registration-page.fxml"));
            Parent parent = fxmlLoader.load();

            RegistrationController registrationController = fxmlLoader.getController();
            registrationController.setUserController(userController);
            registrationController.setStage(stage);

            Scene scene = emailField.getScene();
            scene.setRoot(parent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}