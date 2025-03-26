package view.controllers.pages.user;

import controller.UserController;
import controller.LocaleController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.controllers.components.SidebarViewController;
import java.util.ResourceBundle;

import java.io.IOException;

public class LoginViewController {
    public VBox loginVBox;
    public Label loginLabel;
    public HBox buttonsHBox;
    public Button loginButton;
    public Button registerButton;
    private UserController userController;
    private SidebarViewController sidebarViewController;
    private ResourceBundle viewText;
    private LocaleController localeController;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    public void setControllers(UserController userController, SidebarViewController sidebarViewController) {
        this.userController = userController;
        this.sidebarViewController = sidebarViewController;
    }

    @FXML
    private void initialize() {
        if (viewText != null) {
            loginLabel.setText(viewText.getString("login.title"));
            loginButton.setText(viewText.getString("login.login"));
            registerButton.setText(viewText.getString("login.register"));
            emailField.setText(viewText.getString("login.username"));
            passwordField.setText(viewText.getString("login.password"));
        }
    }

    @FXML
    private void handleLogin() {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(viewText.getString("error.title"), viewText.getString("error.invalidLogin"));
            return;
        }

        try {
            if (userController.authenticateUser(username, password)) {
                sidebarViewController.updateUserButtons();
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.close();
            } else {
                showAlert(viewText.getString("error.title"), viewText.getString("error.invalidCredentials"));
            }
        } catch (Exception e) {
            showAlert(viewText.getString("error.title"), viewText.getString("error.unexpectedError"));
        }
    }

    @FXML
    private void goToRegisterPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/registration-page.fxml"));
            Parent parent = fxmlLoader.load();

            RegistrationViewController registrationViewController = fxmlLoader.getController();
            registrationViewController.setControllers(userController, sidebarViewController);

            Scene scene = emailField.getScene();
            scene.setRoot(parent);

        } catch (IOException e) {
            showAlert(viewText.getString("error.title"), viewText.getString("error.unexpectedError") + e.getMessage());
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