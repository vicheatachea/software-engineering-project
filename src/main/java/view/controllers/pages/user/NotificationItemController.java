package view.controllers.pages.user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class NotificationItemController {
    @FXML
    private Label eventLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private HBox notificationItem;

    private NotificationsController parentController;

    public void setNotificationData(NotificationsController parent, String event, int time) {
        this.parentController = parent;
        eventLabel.setText(event);
        timeLabel.setText("Time: " + time);
    }

    @FXML
    private void handleDelete() {
        if (parentController != null) {
            parentController.removeNotification(notificationItem);
        }
    }
}
