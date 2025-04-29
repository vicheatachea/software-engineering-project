package view.controllers.components;

import controller.BaseController;
import controller.LocaleController;
import controller.UserController;
import controller.notifications.NotificationAware;
import controller.notifications.NotificationService;
import dto.Event;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.controllers.ControllerAware;
import view.controllers.pages.user.LoginViewController;
import view.controllers.pages.user.NotificationsViewController;
import view.controllers.pages.user.UserProfileViewController;


public class SidebarViewController implements ControllerAware, NotificationAware {
	private static final Logger logger = LoggerFactory.getLogger(SidebarViewController.class);

	private static final String TIMETABLE = "timetable";

	private final StringProperty currentView = new SimpleStringProperty();
	private final List<EventNotification> notifications = new ArrayList<>();
	private NotificationService notificationService;
	private BaseController baseController;
	private LocaleController localeController;
	private UserController userController;
	private Button accountButton;
	private Button loginButton;
	private Button notificationsButton;
	private ResourceBundle viewText;

	@FXML
	private VBox sidebar;
	private Button timetableButton;
	private Button groupsButton;
	private Button subjectsButton;
	private Button locationsButton;
	private Button settingsButton;
	private Button quitButton;

	@FXML
	private void initialize() {
		Platform.runLater(() -> {
			ResourceBundle initialViewText = localeController.getUIBundle();

			addButton(initialViewText.getString("sidebar.timetable"), TIMETABLE);
			addButton(initialViewText.getString("sidebar.groups"), "groups");
			addButton(initialViewText.getString("sidebar.subjects"), "subjects");
			addButton(initialViewText.getString("sidebar.locations"), "locations");
			addButton(initialViewText.getString("sidebar.settings"), "settings");
			addButton(initialViewText.getString("sidebar.quit"), "quit");

			currentView.set(TIMETABLE);
			timetableButton = (Button) sidebar.lookup("#timetableButton");
			groupsButton = (Button) sidebar.lookup("#groupsButton");
			subjectsButton = (Button) sidebar.lookup("#subjectsButton");
			locationsButton = (Button) sidebar.lookup("#locationsButton");
			settingsButton = (Button) sidebar.lookup("#settingsButton");
			quitButton = (Button) sidebar.lookup("#quitButton");

			try {
				HBox userArea = FXMLLoader.load(getClass().getResource("/layouts/components/sidebar/user-area.fxml"));

				sidebar.getChildren().add(1, userArea);

				accountButton = (Button) userArea.lookup("#accountButton");
				loginButton = (Button) userArea.lookup("#loginButton");
				notificationsButton = (Button) userArea.lookup("#notificationsButton");

				accountButton.setOnAction(event -> showUserProfilePopup());
				loginButton.setOnAction(event -> showLoginPopup());
				notificationsButton.setOnAction(event -> showNotificationPopup());

				accountButton.setText(initialViewText.getString("sidebar.account"));
				loginButton.setText(initialViewText.getString("sidebar.login"));
				notificationsButton.setText(initialViewText.getString("sidebar.notifications"));

				updateUserButtons();
			} catch (IOException e) {
				logger.error("Error initializing: {}", e.getMessage());
			}
		});
	}

	public StringProperty currentViewProperty() {
		return currentView;
	}

	@Override
	public void setBaseController(BaseController baseController) {
		this.baseController = baseController;
		this.localeController = baseController.getLocaleController();
		this.userController = baseController.getUserController();
		this.notificationService = new NotificationService(this, baseController.getEventController());
		this.viewText = baseController.getLocaleController().getUIBundle();
	}

	private void addButton(String name, String view) {
		try {
			Button button = FXMLLoader.load(getClass().getResource("/layouts/components/sidebar/sidebar-button.fxml"));

			button.setText(name);
			button.setId(view + "Button");
			button.setOnAction(event -> currentView.set(view));
			VBox.setMargin(button, new Insets(30, 0, 0, 0));
			sidebar.getChildren().add(button);
		} catch (IOException e) {
			logger.error("Error adding a button: {}", e.getMessage());
		}
	}

	public void updateUserButtons() {
		boolean isLoggedIn = userController.isUserLoggedIn();
		boolean isTeacher = userController.isCurrentUserTeacher();

		accountButton.setVisible(isLoggedIn);
		accountButton.setManaged(isLoggedIn);
		loginButton.setVisible(!isLoggedIn);
		loginButton.setManaged(!isLoggedIn);
		notificationsButton.setVisible(isLoggedIn);
		notificationsButton.setManaged(isLoggedIn);

		subjectsButton.setVisible(isTeacher);
		subjectsButton.setManaged(isTeacher);
		locationsButton.setVisible(isTeacher);
		locationsButton.setManaged(isTeacher);

		clearNotifications();
		currentView.set("clear");
		currentView.set(TIMETABLE);
	}

	private void showLoginPopup() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/login-page.fxml"));
			Parent parent = fxmlLoader.load();

			LoginViewController loginViewController = fxmlLoader.getController();
			loginViewController.setBaseController(baseController);
			loginViewController.setSidebarViewController(this);

			Stage loginStage = new Stage();
			loginStage.initModality(Modality.APPLICATION_MODAL);
			loginStage.initOwner(sidebar.getScene().getWindow());
			loginStage.setTitle(viewText.getString("login.title"));
			loginStage.setScene(new Scene(parent));
			loginStage.showAndWait();
		} catch (IOException e) {
			logger.error("Error showing login popup: {}", e.getMessage());
		}
	}

	private void showUserProfilePopup() {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/layouts/pages/user/user-profile-page.fxml"));
			Parent parent = fxmlLoader.load();

			UserProfileViewController userProfileViewController = fxmlLoader.getController();
			userProfileViewController.setBaseController(baseController);
			userProfileViewController.setSidebarViewController(this);
			userProfileViewController.updateUserInfo();

			Stage userProfileStage = new Stage();
			userProfileStage.initModality(Modality.APPLICATION_MODAL);
			userProfileStage.initOwner(sidebar.getScene().getWindow());
			userProfileStage.setTitle(viewText.getString("userprofile.title"));
			userProfileStage.setScene(new Scene(parent));
			userProfileStage.showAndWait();
		} catch (IOException e) {
			logger.error("Error showing profile popup: {}", e.getMessage());
		}
	}

	private void showNotificationPopup() {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(getClass().getResource("/layouts/pages/user/notifications-page.fxml"));
			Parent parent = fxmlLoader.load();

			NotificationsViewController notificationsViewController = fxmlLoader.getController();
			notificationsViewController.setBaseController(baseController);
			notificationsViewController.setEventNotifications(notifications);

			Stage notificationStage = new Stage();
			notificationStage.initModality(Modality.APPLICATION_MODAL);
			notificationStage.initOwner(sidebar.getScene().getWindow());
			notificationStage.setTitle(viewText.getString("notifications.title"));
			notificationStage.setScene(new Scene(parent));
			notificationStage.showAndWait();
		} catch (IOException e) {
			logger.error("Error showing notification popup: {}", e.getMessage());
		}
	}

	@Override
	public void notify(Event event, int time) {
		notifications.add(new EventNotification(event, time));
	}

	private void clearNotifications() {
		notifications.clear();
		notificationService.reset();
	}

	public void shutdownNotifications() {
		notificationService.shutdown();
	}

	public void updateTranslations() {
		ResourceBundle updateViewText = localeController.getUIBundle();

		accountButton.setText(updateViewText.getString("sidebar.account"));
		loginButton.setText(updateViewText.getString("sidebar.login"));
		notificationsButton.setText(updateViewText.getString("sidebar.notifications"));

		timetableButton.setText(updateViewText.getString("sidebar.timetable"));
		groupsButton.setText(updateViewText.getString("sidebar.groups"));
		subjectsButton.setText(updateViewText.getString("sidebar.subjects"));
		locationsButton.setText(updateViewText.getString("sidebar.locations"));
		settingsButton.setText(updateViewText.getString("sidebar.settings"));
		quitButton.setText(updateViewText.getString("sidebar.quit"));
	}
}