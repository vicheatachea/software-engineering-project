package view.controllers.pages.user;

import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class NotificationItemViewController {
    @FXML
    private Label eventLabel;
    @FXML
    private HBox notificationItem;

    private NotificationsViewController parentController;

    public void setNotificationData(NotificationsViewController parent, Event event, int time) {
        this.parentController = parent;
        if (event instanceof AssignmentDTO assignmentDTO) {
            eventLabel.setText("Assignment \"" + assignmentDTO.assignmentName() + "\" is due in " + time + " minutes.");
        } else if (event instanceof TeachingSessionDTO teachingSessionDTO) {
            eventLabel.setText("Class of " + teachingSessionDTO.subjectCode() + " is starting in " + time + " minutes.");
        }
    }

    @FXML
    private void handleDelete() {
        if (parentController != null) {
            parentController.removeNotification(notificationItem);
        }
    }
}
