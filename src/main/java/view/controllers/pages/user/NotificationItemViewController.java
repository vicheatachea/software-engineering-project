package view.controllers.pages.user;

import controller.UserController;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import controller.BaseController;
import java.util.ResourceBundle;

public class NotificationItemViewController {
    @FXML
    private Label eventLabel;


    @FXML
    private HBox notificationItem;
    private ResourceBundle viewText;

    private NotificationsViewController parentController;
    private BaseController baseController;
    private UserController userController;

    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
        this.userController = baseController.getUserController();
        this.viewText = baseController.getLocaleController().getUIBundle();
    }

    public void setNotificationData(NotificationsViewController parent, Event event, int time, ResourceBundle viewText) {
        this.parentController = parent;

        if (event instanceof AssignmentDTO assignmentDTO) {
            eventLabel.setText(String.format(viewText.getString("notificationItem.assignmentText"), assignmentDTO.assignmentName(), time));
        } else if (event instanceof TeachingSessionDTO teachingSessionDTO) {
            eventLabel.setText(String.format(viewText.getString("notificationItem.classOfText"), teachingSessionDTO.subjectCode(), time));
        }
    }



    @FXML
    private void handleDelete() {
        if (parentController != null) {
            parentController.removeNotification(notificationItem);
        }
    }
}
