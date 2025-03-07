package view.controllers.pages.user;

import controller.UserController;
import dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class UserProfileController {
	private UserController userController;

	@FXML
	private ImageView profileImageView;
	@FXML
	private Label nameLabel;
	@FXML
	private Label roleLabel;
	@FXML
	private Label socialNumberLabel;

	public void setUserController(UserController userController) {
		this.userController = userController;
	}

	@FXML
	private void handleLogout() {
		userController.logout();
		Stage stage = (Stage) nameLabel.getScene().getWindow();
		stage.close();
	}

	@FXML
	public void initialize() {
	}

	@FXML
	public void updateUserInfo() {
		if (userController == null || !userController.isUserLoggedIn()) {
			showAlert("Account Error", "You are not logged in.");
			return;
		}

		UserDTO userDTO = userController.getLoggedInUser();

		if (userDTO == null) {
			showAlert("Account Error", "Your account information could not be found. You have been logged out.");
			userController.logout();
			return;
		}

		nameLabel.setText(userDTO.firstName() + " " + userDTO.lastName());
		roleLabel.setText(userDTO.role());
		socialNumberLabel.setText(userDTO.socialNumber());


	}


	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

}