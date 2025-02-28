package view.controllers.pages.user;

import controller.UserController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RegistrationController {
    private UserController userController;
    private Stage stage;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField socialNumberField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField dateOfBirthField;
    @FXML
    private ComboBox<String> roleComboBox;

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleRegister() {
        try {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String socialNumber = socialNumberField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            LocalDate dateOfBirth = LocalDate.parse(dateOfBirthField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String role = roleComboBox.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || socialNumber.isEmpty() || username.isEmpty() ||
                    password.isEmpty() || dateOfBirthText.isEmpty() || role == null) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            UserDTO userDTO = new UserDTO(username, password, "salt", firstName, lastName, dateOfBirth, socialNumber, role);

            if (userController.registerUser(userDTO)) {
                stage.close();
            } else {
                System.out.println("Invalid user data.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLoginPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/login-page.fxml"));
            Parent parent = fxmlLoader.load();

            LoginController loginController = fxmlLoader.getController();
            loginController.setUserController(userController);
            loginController.setStage(stage);

            Scene scene = firstNameField.getScene();
            scene.setRoot(parent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("STUDENT", "TEACHER"));
    }
}