package view.controllers.pages.user;

import controller.BaseController;
import controller.UserController;
import dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.controllers.components.SidebarViewController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegistrationViewController {
	public VBox registrationVBox;
	public Label registerLabel;
	public TextField emailField;
	public HBox dobRoleHBox;
	public HBox buttonsHBox;
	public Button backToLoginButton;
	public Button registerButton;
	private UserController userController;
	private SidebarViewController sidebarViewController;


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
	private DatePicker dobPicker;
	@FXML
	private ComboBox<String> roleComboBox;

	public void setControllers(UserController userController, SidebarViewController sidebarViewController) {
		this.userController = userController;
		this.sidebarViewController = sidebarViewController;
	}

	@FXML
	private void handleRegister() {
		try {
			String firstName = firstNameField.getText();
			String lastName = lastNameField.getText();
			String socialNumber = socialNumberField.getText();
			String username = usernameField.getText();
			String password = passwordField.getText();
			LocalDateTime dateOfBirth = dobPicker.getValue().atTime(0, 0);
			String role = roleComboBox.getValue();

			if (firstName.isEmpty() || lastName.isEmpty() || socialNumber.isEmpty() || username.isEmpty() ||
			    password.isEmpty() || dobPicker.getValue() == null || role == null) {
				showAlert("Error", "Please fill in all fields.");
				return;
			}

			if (!firstName.matches("^[a-zA-Z]+$") || !lastName.matches("^[a-zA-Z]+$")) {
				showAlert("Warning", "First name and last name can only contain letters.");
				return;
			}

			if (socialNumber.length() != 11) {
				showAlert("Warning", "Social number has to be exactly 11 characters long.");
				return;
			}

			if (userController.isUsernameTaken(username)) {
				showAlert("Error", "Username is already taken.");
				return;
			}

			if (password.length() < 8) {
				showAlert("Warning", "Password must be 8 characters long.");
				return;
			}

			UserDTO userDTO =
					new UserDTO(username, password, firstName, lastName, dateOfBirth, socialNumber.toUpperCase(), role);

			if (userController.registerUser(userDTO)) {
				userController.authenticateUser(username, password);
				sidebarViewController.updateUserButtons();
				Stage stage = (Stage) firstNameField.getScene().getWindow();
				stage.close();
			} else {
				showAlert("Error", "Invalid user data.");
			}
		} catch (Exception e) {
			showAlert("Error", "An unexpected error occurred: " + e.getMessage());
		}
	}

	@FXML
	private void goToLoginPage() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/login-page.fxml"));
			Parent parent = fxmlLoader.load();

			LoginViewController loginViewController = fxmlLoader.getController();
			loginViewController.setControllers(userController, sidebarViewController);

			Scene scene = firstNameField.getScene();
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

	@FXML
	public void initialize() {
		roleComboBox.setItems(FXCollections.observableArrayList("STUDENT", "TEACHER"));

		// Setup the DatePicker to disable today's and future dates
		dobPicker.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				if (date.compareTo(LocalDate.now()) >= 0) {
					setDisable(true);
					setStyle("-fx-background-color: #FFE0E9;");
				}
			}
		});
	}
}