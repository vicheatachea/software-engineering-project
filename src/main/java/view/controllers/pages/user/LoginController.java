package view.controllers.pages.user;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class LoginController {
    private Controller controller;
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (authenticateUser(username, password)) {
            controller.setUserLoggedIn(true);
            stage.close(); // Close the login popup
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        return username.equals("admin") && password.equals("password"); // Replace with actual authentication logic
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/registration-page.fxml"));
            Parent parent = fxmlLoader.load();

            RegistrationController registerController = fxmlLoader.getController();
            registerController.setController(controller);
            registerController.setStage(stage); // Use the same stage as login
            Scene scene = emailField.getScene(); // Get current scene
            scene.setRoot(parent); // Replace the scene root with registration page

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}