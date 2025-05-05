package view.controllers.pages.user;

import controller.BaseController;
import controller.UserController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.controllers.ControllerAware;
import view.controllers.SidebarControllerAware;
import view.controllers.components.SidebarViewController;

import java.io.IOException;
import java.util.ResourceBundle;

public class LoginViewController implements ControllerAware, SidebarControllerAware {
	private static final String ERROR_TITLE = "error.title";

	private BaseController baseController;
	private UserController userController;
	private SidebarViewController sidebarViewController;
	private ResourceBundle viewText;

	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label emailLabel;
	@FXML
	private Label passwordLabel;
	@FXML
	private VBox loginVBox;
	@FXML
	private Label loginLabel;
	@FXML
	private HBox buttonsHBox;
	@FXML
	private Button loginButton;
	@FXML
	private Button loginRegisterButton;

	@Override
	public void setBaseController(BaseController baseController) {
		this.baseController = baseController;
		this.userController = baseController.getUserController();
		this.viewText = baseController.getLocaleController().getUIBundle();
	}

	@Override
	public void setSidebarViewController(SidebarViewController sidebarViewController) {
		this.sidebarViewController = sidebarViewController;
	}

	@FXML
	private void initialize() {
		Platform.runLater(() -> {
			loginLabel.setText(viewText.getString("login.title"));
			loginButton.setText(viewText.getString("login.login"));
			loginRegisterButton.setText(viewText.getString("login.register"));
			emailLabel.setText(viewText.getString("login.username"));
			passwordLabel.setText(viewText.getString("login.password"));

		});
	}

	@FXML
	private void handleLogin() {
		String username = emailField.getText();
		String password = passwordField.getText();

		if (username.isEmpty() || password.isEmpty()) {
			showAlert(viewText.getString(ERROR_TITLE), viewText.getString("error.fillAllFields"));
			return;
		}

		try {
			if (userController.authenticateUser(username, password)) {
				sidebarViewController.updateUserButtons();
				Stage stage = (Stage) emailField.getScene().getWindow();
				stage.close();
			} else {
				showAlert(viewText.getString(ERROR_TITLE), viewText.getString("error.invalidLogin"));
			}
		} catch (Exception e) {
			showAlert(viewText.getString(ERROR_TITLE), viewText.getString("error.unexpectedError"));
		}
	}

	@FXML
	private void goToRegisterPage() {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/layouts/pages/user/registration-page.fxml"));
			Parent parent = fxmlLoader.load();

			RegistrationViewController registrationViewController = fxmlLoader.getController();
			registrationViewController.setBaseController(baseController);
			registrationViewController.setSidebarViewController(sidebarViewController);

			Stage stage = (Stage) emailField.getScene().getWindow();
			stage.setTitle(viewText.getString("register.title"));
			Scene scene = emailField.getScene();
			scene.setRoot(parent);
		} catch (IOException e) {
			showAlert(viewText.getString(ERROR_TITLE), viewText.getString("error.unexpectedError"));
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