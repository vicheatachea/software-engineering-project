package view.controllers.components;

import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class EventPopupController {
    private Event event;
    private final RowConstraints hiddenRow = new RowConstraints(0);

    @FXML
    private GridPane popupGridPane;
    @FXML
    private ComboBox eventComboBox;
    @FXML
    private ComboBox scheduleComboBox;
    @FXML
    private ComboBox groupComboBox;
    @FXML
    private ComboBox subjectComboBox;
    @FXML
    private ComboBox assignmentComboBox;
    @FXML
    private ComboBox locationComboBox;
    @FXML
    private TextField nameTextField;
    @FXML
    private HBox startHBox;
    @FXML
    private HBox endHBox;
    @FXML
    private Label startLabel;
    @FXML
    private Label endLabel;
    @FXML
    private Label groupLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label assignmentLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    public void setUp(Event event) {
        this.event = event;
    }

    @FXML
    private void initialize() {
        startHBox.getChildren().add(new TimeTextField());
        endHBox.getChildren().add(new TimeTextField());

        eventComboBox.getItems().addAll("Class", "Assignment");
        scheduleComboBox.getItems().addAll("Myself", "Group");
        assignmentComboBox.getItems().addAll("Individual", "Group");

        eventComboBox.addEventHandler(ActionEvent.ACTION, event -> handleEventChange());
        scheduleComboBox.addEventHandler(ActionEvent.ACTION, event -> handleScheduleChange());

        Platform.runLater(() -> {
            if (event == null) {
                eventComboBox.setValue("Class");
                scheduleComboBox.setValue("Myself");
                return;
            }

            eventComboBox.setDisable(true);

            if (event instanceof TeachingSessionDTO) {
                eventComboBox.setValue("Class");
            } else if (event instanceof AssignmentDTO) {
                eventComboBox.setValue("Assignment");
            }
        });
    }

    private void handleEventChange() {
        String eventType = (String) eventComboBox.getValue();

        if (eventType == null) {
            return;
        }

        switch (eventType) {
            case "Class":
                startLabel.setText("Start Time");
                endLabel.setText("End Time");
                endDatePicker.setVisible(false);
                endDatePicker.setManaged(false);

                popupGridPane.getRowConstraints().set(6, hiddenRow);
                nameLabel.setVisible(false);
                nameTextField.setVisible(false);

                popupGridPane.getRowConstraints().set(7, hiddenRow);
                assignmentLabel.setVisible(false);
                assignmentComboBox.setVisible(false);

                popupGridPane.getRowConstraints().set(8, new RowConstraints());
                locationLabel.setVisible(true);
                locationComboBox.setVisible(true);
                break;
            case "Assignment":
                startLabel.setText("Publishing Date");
                endLabel.setText("Due Date");
                endDatePicker.setVisible(true);
                endDatePicker.setManaged(true);

                popupGridPane.getRowConstraints().set(6, new RowConstraints());
                nameLabel.setVisible(true);
                nameTextField.setVisible(true);

                popupGridPane.getRowConstraints().set(7, new RowConstraints());
                assignmentLabel.setVisible(true);
                assignmentComboBox.setVisible(true);

                popupGridPane.getRowConstraints().set(8, hiddenRow);
                locationLabel.setVisible(false);
                locationComboBox.setVisible(false);
                break;
        }
    }

    private void handleScheduleChange() {
        String scheduleType = (String) scheduleComboBox.getValue();

        if (scheduleType == null) {
            return;
        }

        switch (scheduleType) {
            case "Myself":
                popupGridPane.getRowConstraints().set(4, hiddenRow);
                groupLabel.setVisible(false);
                groupComboBox.setVisible(false);
                break;
            case "Group":
                popupGridPane.getRowConstraints().set(4, new RowConstraints());
                groupLabel.setVisible(true);
                groupComboBox.setVisible(true);
                break;
        }
    }

    @FXML
    private void handleSaveEvent() {
        String eventType = (String) eventComboBox.getValue();

        if (eventType == null) {
            displayErrorAlert("Please select an event type");
            return;
        }

        switch (eventType) {
            case "Class":
                // Save class event
                break;
            case "Assignment":
                // Save assignment event
                break;
            default:
                // Save default event
                break;
        }
    }

    private void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Configuration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
