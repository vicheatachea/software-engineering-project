package view.controllers.pages.user;

import controller.UserController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import view.controllers.components.SidebarViewController;

import java.io.IOException;

public class LoginViewController {
    private UserController userController;
    private SidebarViewController sidebarViewController;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    public void setViewControllers(UserController userController, SidebarViewController sidebarViewController) {
        this.userController = userController;
        this.sidebarViewController = sidebarViewController;
    }

    @FXML
    private void handleLogin() {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try {
            if (userController.authenticateUser(username, password)) {
                sidebarViewController.updateUserButtons();
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Error", "Invalid credentials.");
            }
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegisterPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/registration-page.fxml"));
            Parent parent = fxmlLoader.load();

            RegistrationViewController registrationViewController = fxmlLoader.getController();
            registrationViewController.setViewControllers(userController, sidebarViewController);

            Scene scene = emailField.getScene();
            scene.setRoot(parent);

        } catch (IOException e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}