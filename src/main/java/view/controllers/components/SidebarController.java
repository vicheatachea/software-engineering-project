package view.controllers.components;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.io.IOException;

public class SidebarController {
    private final StringProperty currentView = new SimpleStringProperty();
    private int notificationCount = 0;

    @FXML
    private VBox sidebar;
    private Button notificationButton;

    @FXML
    private void handleRegister() {
        currentView.set("register");
    }

    @FXML
    private void initialize() {
        // addButton("\uD83D\uDD14", "notifications");
        // addButton("\uD83C\uDFE0 Home", "home");
        addNotificationButton();
        addButton("\uD83D\uDC64 Account", "account");
        addButton("\uD83D\uDCC6 Timetable", "timetable");
        addButton("\uD83D\uDC65 Groups", "groups");
        addButton("\uD83D\uDCDA Subjects", "subjects");
        addButton("\uD83C\uDFE2 Locations", "locations");
        addButton("\uD83D\uDEE0 Settings", "settings");
        addButton("\uD83D\uDEAA Quit", "quit");

        Platform.runLater(() -> currentView.set("timetable"));
    }

    public StringProperty currentViewProperty() {
        return currentView;
    }

    private void addButton(String name, String view) {
        try {
            Button button = FXMLLoader.load(getClass().getResource("/layouts/components/sidebar/sidebar-button.fxml"));

            button.setText(name);
            button.setOnAction(event -> currentView.set(view));
            VBox.setMargin(button, new Insets(30, 0 , 0, 0));
            sidebar.getChildren().add(button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNotificationButton() {
        notificationButton = new Button("\uD83D\uDD14");
        notificationButton.setOnAction(event -> currentView.set("notifications"));
        notificationButton.getStyleClass().add("icon-button");
        VBox.setMargin(notificationButton, new Insets(30, 0 , 0, 0));
        sidebar.getChildren().add(notificationButton);
    }

    public void incrementNotificationCount() {
        notificationCount++;
        updateNotificationButton();
    }

    public void resetNotificationCount() {
        notificationCount = 0;
        updateNotificationButton();
    }

    private void updateNotificationButton() {
        if (notificationCount > 0) {
            notificationButton.setText("\uD83D\uDD14 " + notificationCount);
        } else {
            notificationButton.setText("\uD83D\uDD14");
        }
    }

    public void updateAccountButtonText(boolean isLoggedIn) {
        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getText().contains("Account")) {
                    button.setText(isLoggedIn ? "\uD83D\uDC64 My Account" : "\uD83D\uDC64 Account");
                    break;
                }
            }
        }
    }
}