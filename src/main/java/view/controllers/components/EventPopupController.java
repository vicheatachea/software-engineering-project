package view.controllers.components;

import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class EventPopupController {
    private Event event;

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

    public void setUp(Event event) {
        this.event = event;
    }

    @FXML
    private void initialize() {
        eventComboBox.getItems().addAll("Class", "Assignment");
        scheduleComboBox.getItems().addAll("Myself", "Group");
        assignmentComboBox.getItems().addAll("Individual", "Group");

        if (event instanceof AssignmentDTO) {
            // Set up assignment fields
        } else if (event instanceof TeachingSessionDTO) {
            // Set up class fields
        } else {
            // Set up default fields
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
