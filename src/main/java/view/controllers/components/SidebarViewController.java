package view.controllers.components;

import controller.BaseController;
import controller.UserController;
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

public class SidebarViewController implements ControllerAware {
    private final StringProperty currentView = new SimpleStringProperty();
    private int notificationCount = 0;

    private UserController userController;
    private Button accountButton;
    private Button loginButton;
    private Button notificationsButton;
    private Button subjectsButton;
    private Button locationsButton;

    @FXML
    private VBox sidebar;

    @FXML
    private void initialize() {
        addButton("\uD83D\uDCC6 Timetable", "timetable");
        addButton("\uD83D\uDC65 Groups", "groups");
        addButton("\uD83D\uDCDA Subjects", "subjects");
        addButton("\uD83C\uDFE2 Locations", "locations");
        addButton("\uD83D\uDEE0 Settings", "settings");
        addButton("\uD83D\uDEAA Quit", "quit");

        Platform.runLater(() -> {
            currentView.set("timetable");
            subjectsButton = (Button) sidebar.lookup("#subjectsButton");
            locationsButton = (Button) sidebar.lookup("#locationsButton");

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
        this.userController = baseController.getUserController();
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

//    public void incrementNotificationCount() {
//        notificationCount++;
//        updateNotificationButton();
//    }
//
//    public void resetNotificationCount() {
//        notificationCount = 0;
//        updateNotificationButton();
//    }
//
//    private void updateNotificationButton() {
//        if (notificationCount > 0) {
//            notificationButton.setText("\uD83D\uDD14 " + notificationCount);
//        } else {
//            notificationButton.setText("\uD83D\uDD14");
//        }
//    }
}