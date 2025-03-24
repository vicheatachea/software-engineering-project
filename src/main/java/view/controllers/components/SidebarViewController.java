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
import view.controllers.ControllerAware;
import view.controllers.pages.user.LoginViewController;
import view.controllers.pages.user.NotificationsViewController;
import view.controllers.pages.user.UserProfileViewController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SidebarViewController implements ControllerAware, NotificationAware {
    private final StringProperty currentView = new SimpleStringProperty();
    private NotificationService notificationService;
    private List<EventNotification> notifications = new ArrayList<>();

    private LocaleController localeController;
    private UserController userController;
    private Button accountButton;
    private Button loginButton;
    private Button notificationsButton;

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
            ResourceBundle viewText = localeController.getUIBundle();

            addButton("\uD83D\uDCC6 " + viewText.getString("sidebar.timetable"), "timetable");
            addButton("\uD83D\uDC65 " + viewText.getString("sidebar.groups"), "groups");
            addButton("\uD83D\uDCDA " + viewText.getString("sidebar.subjects"), "subjects");
            addButton("\uD83C\uDFE2 " + viewText.getString("sidebar.locations"), "locations");
            addButton("\uD83D\uDEE0 " + viewText.getString("sidebar.settings"), "settings");
            addButton("\uD83D\uDEAA " + viewText.getString("sidebar.quit"), "quit");

            currentView.set("timetable");
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

                updateUserButtons();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public StringProperty currentViewProperty() {
        return currentView;
    }

    @Override
    public void setBaseController(BaseController baseController) {
        this.localeController = baseController.getLocaleController();
        this.userController = baseController.getUserController();
        this.notificationService = new NotificationService(this, baseController.getEventController());
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
            e.printStackTrace();
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
        currentView.set("timetable");
    }

    private void showLoginPopup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/login-page.fxml"));
            Parent parent = fxmlLoader.load();

            LoginViewController loginViewController = fxmlLoader.getController();
            loginViewController.setControllers(userController, this);

            Stage loginStage = new Stage();
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initOwner(sidebar.getScene().getWindow());
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(parent));
            loginStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showUserProfilePopup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/user-profile-page.fxml"));
            Parent parent = fxmlLoader.load();

            UserProfileViewController userProfileViewController = fxmlLoader.getController();
            userProfileViewController.setControllers(userController, this);
            userProfileViewController.updateUserInfo();

            Stage userProfileStage = new Stage();
            userProfileStage.initModality(Modality.APPLICATION_MODAL);
            userProfileStage.initOwner(sidebar.getScene().getWindow());
            userProfileStage.setTitle("User Profile");
            userProfileStage.setScene(new Scene(parent));
            userProfileStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNotificationPopup() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/pages/user/notifications-page.fxml"));
            Parent parent = fxmlLoader.load();

            NotificationsViewController notificationsViewController = fxmlLoader.getController();
            notificationsViewController.setEventNotifications(notifications);

            Stage notificationStage = new Stage();
            notificationStage.initModality(Modality.APPLICATION_MODAL);
            notificationStage.initOwner(sidebar.getScene().getWindow());
            notificationStage.setTitle("Notifications");
            notificationStage.setScene(new Scene(parent));
            notificationStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
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
        ResourceBundle viewText = localeController.getUIBundle();

        timetableButton.setText("\uD83D\uDCC6 " + viewText.getString("sidebar.timetable"));
        groupsButton.setText("\uD83D\uDC65 " + viewText.getString("sidebar.groups"));
        subjectsButton.setText("\uD83D\uDCDA " + viewText.getString("sidebar.subjects"));
        locationsButton.setText("\uD83C\uDFE2 " + viewText.getString("sidebar.locations"));
        settingsButton.setText("\uD83D\uDEE0 " + viewText.getString("sidebar.settings"));
        quitButton.setText("\uD83D\uDEAA " + viewText.getString("sidebar.quit"));
    }
}