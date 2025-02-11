package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import service.UserService;
import util.SceneSwitcher;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UserService userService = new UserService();


    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (userService.loginUser(email, password)) {
            showAlert("Success", "Login successful!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Invalid credentials!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToRegisterPage() {
        SceneSwitcher.switchScene((Stage) emailField.getScene().getWindow(), "/views/register.fxml");
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}
