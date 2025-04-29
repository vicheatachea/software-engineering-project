package view.controllers.pages.user;

import controller.BaseController;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import view.controllers.ControllerAware;


public class NotificationItemViewController implements ControllerAware {
    private ResourceBundle viewText;
    private NotificationsViewController parentController;

    @FXML
    private Label eventLabel;
    @FXML
    private HBox notificationItem;

    @Override
    public void setBaseController(BaseController baseController) {
        this.viewText = baseController.getLocaleController().getUIBundle();
    }

    public void setNotificationData(NotificationsViewController parent, Event event, int time) {
        this.parentController = parent;

        if (event instanceof AssignmentDTO assignmentDTO) {
            eventLabel.setText(MessageFormat.format(viewText.getString("notificationItem.assignmentText"), assignmentDTO.assignmentName(), time));
        } else if (event instanceof TeachingSessionDTO teachingSessionDTO) {
            eventLabel.setText(MessageFormat.format(viewText.getString("notificationItem.classOfText"), teachingSessionDTO.subjectCode(), time));
        }
    }

    @FXML
    private void handleDelete() {
        if (parentController != null) {
            parentController.removeNotification(notificationItem);
        }
    }
}
