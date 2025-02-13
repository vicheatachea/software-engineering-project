package view.controllers.pages.user;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import util.SceneSwitcher;

public class RegisterController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField socialNumberField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

//    private final UserService userService = new UserService();

    @FXML
    private void handleExit() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

//    @FXML
//    private void handleRegister() {
////        User user = new User();
//        user.setFirstName(firstNameField.getText());
//        user.setLastName(lastNameField.getText());
//        user.setSocialNumber(socialNumberField.getText());
//        user.setEmail(emailField.getText());
//        user.setUsername(usernameField.getText());
//        user.setPassword(passwordField.getText());
//
//        if (userService.registerUser(user)) {
//            showAlert("Success", "Registration successful!", Alert.AlertType.INFORMATION);
//            SceneSwitcher.switchScene((Stage) emailField.getScene().getWindow(), "/views/login.fxml");
//        } else {
//            showAlert("Error", "Email already exists!", Alert.AlertType.ERROR);
//        }
//    }

    @FXML
    private void goToLoginPage() {
        SceneSwitcher.switchScene((Stage) firstNameField.getScene().getWindow(), "/views/login.fxml");
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
