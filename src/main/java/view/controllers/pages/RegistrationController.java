// src/main/java/view/controllers/pages/RegistrationController.java
package view.controllers.pages;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import util.SceneSwitcher;

public class RegistrationController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField socialNumberField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleRegister() {
        // Handle registration logic here
    }

    @FXML
    private void goToLoginPage() {
        SceneSwitcher.switchScene((Stage) firstNameField.getScene().getWindow(), "/layouts/pages/user/login-page.fxml");
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}