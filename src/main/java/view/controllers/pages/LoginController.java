// src/main/java/view/controllers/pages/LoginController.java
package view.controllers.pages;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import util.SceneSwitcher;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        // Handle login logic here
    }

    @FXML
    private void goToRegisterPage() {
        SceneSwitcher.switchScene((Stage) emailField.getScene().getWindow(), "/layouts/pages/user/registration-page.fxml");
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}