package controller;

import dao.UserDAO;
import dto.UserDTO;
import entity.UserEntity;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.UserModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UserController {
    private UserModel userModel;
    private boolean userLoggedIn = false;
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField socialNumberField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField dateOfBirthField;

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label socialNumberLabel;

    public UserController(UserDTO userDTO) {
        this.userModel = new UserModel(userDTO);
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean status) {
        userLoggedIn = status;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = emailField.getText();
        String password = passwordField.getText();

        if (authenticateUser(username, password)) {
            setUserLoggedIn(true);
            stage.close(); // Close the login popup
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        UserDAO userDAO = new UserDAO();
        return userDAO.authenticate(username, password);
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

            UserDTO userDTO = new UserDTO(username, password, "salt", firstName, lastName, dateOfBirth, socialNumber, role);

            // Validate UserDTO
            UserModel userModel = new UserModel(userDTO);
            if (!userModel.isValid()) {
                System.out.println("Invalid user data.");
                return;
            }

            // Persist UserModel
            UserDAO userDAO = new UserDAO();
            userDAO.persist(new UserEntity(userDTO));

            stage.close();
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

            UserController loginController = fxmlLoader.getController();
            loginController.setController(this);
            loginController.setStage(stage);

            Scene scene = emailField.getScene();
            scene.setRoot(parent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegisterPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/registration-page.fxml"));
            Parent parent = fxmlLoader.load();

            UserController registerController = fxmlLoader.getController();
            registerController.setController(this);
            registerController.setStage(stage);

            Scene scene = emailField.getScene();
            scene.setRoot(parent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        stage.close();
    }

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("STUDENT", "TEACHER"));
    }

    @FXML
    private void handleLogout() {
        setUserLoggedIn(false);
        stage.close();
    }

    @FXML
    private void showUserProfilePopup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/user-profile-page.fxml"));
            Parent parent = fxmlLoader.load();

            Stage userProfileStage = new Stage();
            userProfileStage.initModality(Modality.APPLICATION_MODAL);
            userProfileStage.initOwner(stage);
            userProfileStage.setTitle("User Profile");
            userProfileStage.setScene(new Scene(parent));
            userProfileStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateUserProfile() {
        nameLabel.setText(userModel.getFirstName() + " " + userModel.getLastName());
        roleLabel.setText(userModel.getRole());
        socialNumberLabel.setText(userModel.getSocialNumber());
    }
}