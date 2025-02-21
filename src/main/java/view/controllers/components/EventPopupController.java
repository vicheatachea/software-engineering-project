package view.controllers.components;

import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.fxml.FXML;
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
    private void handleSaveData() {
        // Return data back to the timetable controller and close popup
    }
}
