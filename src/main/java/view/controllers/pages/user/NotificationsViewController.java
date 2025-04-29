package view.controllers.pages.user;

import controller.BaseController;
import dto.Event;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.controllers.ControllerAware;
import view.controllers.components.EventNotification;

public class NotificationsViewController implements ControllerAware {
	private static final Logger logger = LoggerFactory.getLogger(NotificationsViewController.class);
	private List<EventNotification> eventNotifications = new ArrayList<>();
	private ResourceBundle viewText;
	private BaseController baseController;

	@FXML
	private VBox notificationsContainer;
	@FXML
	private Button markAllReadButton;
	@FXML
	private Label notificationLabel;
	@FXML
	private VBox notificationsVBox;
	@FXML
	private ScrollPane notificationsScrollPane;

	@Override
	public void setBaseController(BaseController baseController) {
		this.baseController = baseController;
		this.viewText = baseController.getLocaleController().getUIBundle();
	}

	@FXML
	private void initialize() {
		Platform.runLater(() -> {
			markAllReadButton.setText(viewText.getString("notifications.markAllAsRead"));
			notificationLabel.setText(viewText.getString("notifications.title"));
		});
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
			itemViewController.setBaseController(baseController);
			itemViewController.setNotificationData(this, event, time);

			notificationsContainer.getChildren().add(notificationBox);
		} catch (IOException e) {
			logger.error("Error: ", e);
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