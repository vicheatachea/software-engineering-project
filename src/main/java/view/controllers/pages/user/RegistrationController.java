//package view.controllers.pages.user;
//
//import controller.Controller;
//import javafx.fxml.FXML;
//import javafx.scene.control.TextField;
//import javafx.scene.control.PasswordField;
//import javafx.stage.Stage;
//import javafx.scene.control.ComboBox;
//import javafx.collections.FXCollections;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import java.io.IOException;
//
//public class RegistrationController {
////    private Controller controller;
//    private Stage stage;
//
//    @FXML
//    private ComboBox<String> roleComboBox;
//
//    @FXML
//    public void initialize() {
//        roleComboBox.setItems(FXCollections.observableArrayList("STUDENT", "TEACHER"));
//    }
//
//    @FXML
//    private TextField firstNameField;
//
//    @FXML
//    private TextField lastNameField;
//
//    @FXML
//    private TextField socialNumberField;
//
//    @FXML
//    private TextField usernameField;
//
//    @FXML
//    private TextField dateOfBirthField;
//
//    @FXML
//    private TextField emailField;
//
//    @FXML
//    private PasswordField passwordField;
//
////    public void setController(Controller controller) {
////        this.controller = controller;
//    }
//
//    public void setStage(Stage stage) {
//        this.stage = stage;
//    }
//
//    @FXML
//    private void goToLoginPage() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/login-page.fxml"));
//            Parent parent = fxmlLoader.load();
//
//            LoginController loginController = fxmlLoader.getController();
////            loginController.setController(controller);
//            loginController.setStage(stage); // Use the same stage as registration
//
//            // Update the scene instead of creating a new Stage
//            Scene scene = emailField.getScene(); // Get current scene
//            scene.setRoot(parent); // Replace the scene root with login page
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleRegister() {
//
//        String firstName = firstNameField.getText();
//        String lastName = lastNameField.getText();
//        Integer socialNumber = Integer.parseInt(socialNumberField.getText());
//        String email = emailField.getText();
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//        Integer dateOfBirth = Integer.parseInt(dateOfBirthField.getText());
//        String role = roleComboBox.getValue();
//
//        // Add registration logic here
//
//        stage.close(); // Close the registration popup
//    }
//
//    @FXML
//    private void handleExit() {
//        stage.close();
//    }
//}