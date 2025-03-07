package view.controllers.pages.user;

import controller.notifications.NotificationAware;
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
import view.controllers.components.SidebarController;

public class NotificationsController implements NotificationAware {
    @FXML
    private VBox notificationsContainer;

    @FXML
    private Button markAllReadButton;

    private final List<Event> events = new ArrayList<>();
    private SidebarController sidebarController;

    @FXML
    private VBox notificationsVBox;

    @FXML
    private ScrollPane notificationsScrollPane;

    @FXML
    private void initialize() {
        VBox.setVgrow(notificationsScrollPane, javafx.scene.layout.Priority.ALWAYS);
    }

    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    @Override
    public void notify(Event event, int time) {
        events.add(event);
        loadNotification(event.toString(), time);
        if (sidebarController != null) {
//            sidebarController.incrementNotificationCount();
        }
    }

    private void loadNotification(String event, int time) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/notifications/notification-item.fxml"));
            HBox notificationBox = loader.load();

            NotificationItemController controller = loader.getController();
            controller.setNotificationData(this, event, time);

            notificationsContainer.getChildren().add(notificationBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeNotification(HBox notification) {
        notificationsContainer.getChildren().remove(notification);
        events.removeIf(event -> event.toString().equals(((Label) notification.getChildren().get(0)).getText()));
        if (sidebarController != null) {
//            sidebarController.incrementNotificationCount();
        }
    }

    @FXML
    private void handleMarkAllRead() {
        notificationsContainer.getChildren().clear();
        events.clear();
        if (sidebarController != null) {
//            sidebarController.resetNotificationCount();
        }
    }
}