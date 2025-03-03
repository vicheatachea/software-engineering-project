package view.controllers.pages.main;

import controller.BaseController;
import datasource.MariaDBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.controllers.ControllerAware;
import view.controllers.components.SidebarController;
import view.controllers.pages.user.LoginController;
import view.controllers.pages.user.UserProfileController;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
	Stage stage;
	BaseController baseController;

	@FXML
	private SidebarController sidebarController;
	@FXML
	private VBox sidebar;
	@FXML
	private StackPane mainContent;

	public MainPageController() {
		this.baseController = new BaseController();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {


		SplitPane.setResizableWithParent(sidebar, false);
		SplitPane.setResizableWithParent(mainContent, false);

		sidebarController.currentViewProperty().addListener((observableValue, oldValue, newValue) -> {
			switch (newValue) {
				case "account":
					if (baseController.getUserController().isUserLoggedIn()) {
						showUserProfilePopup();
					} else {
						showLoginPopup();
					}
					break;
				case "home":
					loadContent("/layouts/pages/main/home.fxml");
					break;
				case "timetable":
					loadContent("/layouts/pages/main/timetable.fxml");
					break;
				case "groups":
					loadContent("/layouts/pages/main/groups.fxml");
					break;
				case "settings":
					loadContent("/layouts/pages/main/settings.fxml");
					break;
				case "quit":
					try {
						MariaDBConnection.terminate();
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
					stage.close();
					break;
			}
		});
	}

	private void loadContent(String fxmlFilePath) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
			Node content = fxmlLoader.load();
			Object subController = fxmlLoader.getController();

			if (subController instanceof ControllerAware) {
				((ControllerAware) subController).setBaseController(baseController);
			}

			mainContent.getChildren().setAll(content);
		} catch (NullPointerException e) {
			System.out.println("MainPageController loadContent() could not load " + fxmlFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showLoginPopup() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/login-page.fxml"));
			Parent parent = fxmlLoader.load();

			LoginController loginController = fxmlLoader.getController();
			loginController.setUserController(baseController.getUserController());

			Stage loginStage = new Stage();
			loginController.setStage(loginStage);
			loginStage.initModality(Modality.APPLICATION_MODAL);
			loginStage.initOwner(stage);
			loginStage.setTitle("Login");
			loginStage.setScene(new Scene(parent));
			loginStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showUserProfilePopup() {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/layouts/pages/user/user-profile-page.fxml"));
			Parent parent = fxmlLoader.load();
			
			UserProfileController userProfileController = fxmlLoader.getController();
			userProfileController.setUserController(baseController.getUserController());
			userProfileController.setStage(stage);
			userProfileController.updateUserInfo();

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
}