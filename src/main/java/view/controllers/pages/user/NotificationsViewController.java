package view.controllers.pages.user;

import dto.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ScrollPane;
import view.controllers.components.EventNotification;

public class NotificationsViewController {
    public Label notificationLabel;
    private List<EventNotification> eventNotifications = new ArrayList<>();

    @FXML
    private VBox notificationsContainer;
    @FXML
    private Button markAllReadButton;
    @FXML
    private VBox notificationsVBox;
    @FXML
    private ScrollPane notificationsScrollPane;

    @FXML
    private void initialize() {
        VBox.setVgrow(notificationsScrollPane, javafx.scene.layout.Priority.ALWAYS);
    }

    public void setEventNotifications(List<EventNotification> eventNotifications) {
        clearNotifications();

        this.eventNotifications = eventNotifications;
        for (EventNotification eventNotification : eventNotifications) {
            loadNotification(eventNotification.getEvent(), eventNotification.getTime());
        }
    }

    private void loadNotification(Event event, int time) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/pages/user/notification-item.fxml"));
            HBox notificationBox = loader.load();

            NotificationItemViewController itemViewController = loader.getController();
            itemViewController.setNotificationData(this, event, time);

            notificationsContainer.getChildren().add(notificationBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeNotification(HBox notification) {
        int notificationIndex = notificationsContainer.getChildren().indexOf(notification);
        notificationsContainer.getChildren().remove(notificationIndex);
        eventNotifications.remove(notificationIndex);
    }

    @FXML
    private void handleMarkAllRead() {
        clearNotifications();
    }

    private void clearNotifications() {
        notificationsContainer.getChildren().clear();
        eventNotifications.clear();
    }
}