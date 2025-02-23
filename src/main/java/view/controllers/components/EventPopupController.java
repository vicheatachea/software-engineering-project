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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EventPopupController {
    private Event event;
    private final RowConstraints hiddenRow = new RowConstraints(0);
    private final TimeTextField startTimeField = new TimeTextField();
    private final TimeTextField endTimeField = new TimeTextField();

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
    @FXML
    private TextArea descriptionTextArea;

    public void setUp(Event event) {
        this.event = event;
    }

    @FXML
    private void initialize() {
        startHBox.getChildren().add(startTimeField);
        endHBox.getChildren().add(endTimeField);

        eventComboBox.getItems().addAll("Class", "Assignment");
        scheduleComboBox.getItems().addAll("Myself", "Group");
        assignmentComboBox.getItems().addAll("Individual", "Group");

        // Placeholders
        // TODO: Add subjects and locations from database
        subjectComboBox.getItems().addAll("Math", "Science", "English", "History", "Geography");
        locationComboBox.getItems().addAll("Room 1", "Room 2", "Room 3", "Room 4", "Room 5");
        groupComboBox.getItems().addAll("Group 1", "Group 2", "Group 3", "Group 4", "Group 5");

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

        if (checkNullOrEmpty(eventType, "Please select an event type") ||
                checkNullOrEmpty(scheduleComboBox.getValue(), "Please select a schedule type") ||
                checkNullOrEmpty(subjectComboBox.getValue(), "Please select a subject")) {
            return;
        }

        if (eventType.equals("Assignment")) {
            if (checkNullOrEmpty(startDatePicker.getValue(), "Please select a publishing date") ||
                    checkNullOrEmpty(endDatePicker.getValue(), "Please select a due date") ||
                    checkNullOrEmpty(nameTextField.getText(), "Please enter an assignment name") ||
                    checkNullOrEmpty(assignmentComboBox.getValue(), "Please select an assignment type")) {
                return;
            }
        }

        if (eventType.equals("Class")) {
            if (checkNullOrEmpty(startTimeField.getText(), "Please enter a start time") ||
                    checkNullOrEmpty(endTimeField.getText(), "Please enter an end time") ||
                    checkNullOrEmpty(locationComboBox.getValue(), "Please select a location")) {
                return;
            }
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Event newEvent;

        LocalDate startDate = startDatePicker.getValue();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        LocalTime startLocalTime = LocalTime.parse(startTime, timeFormatter);
        LocalTime endLocalTime = LocalTime.parse(endTime, timeFormatter);

        String scheduleFor = (String) scheduleComboBox.getValue();
        String subject = (String) subjectComboBox.getValue();
        String description = descriptionTextArea.getText();

        switch (eventType) {
            case "Class":
                LocalDateTime startDateTime = LocalDateTime.of(startDate, startLocalTime);
                LocalDateTime endDateTime = LocalDateTime.of(startDate, endLocalTime);

                String location = (String) locationComboBox.getValue();

                newEvent = new TeachingSessionDTO(startDateTime, endDateTime, location, subject, description);
                break;
            case "Assignment":
                LocalDate endDate = endDatePicker.getValue();
                LocalDateTime publishingDateTime = LocalDateTime.of(startDate, startLocalTime);
                LocalDateTime deadlineDateTime = LocalDateTime.of(endDate, endLocalTime);

                String assignmentName = nameTextField.getText();
                String assignmentType = (String) assignmentComboBox.getValue();

                newEvent = new AssignmentDTO(assignmentType, publishingDateTime, deadlineDateTime, assignmentName, subject, description);
                break;
        }

        if (scheduleFor.equals("Myself")) {
            // Save teaching session for myself
        } else {
            String group = (String) groupComboBox.getValue();
            // Save teaching session for group
        }
    }

    private boolean checkNullOrEmpty(Object object, String message) {
        if (object == null || object instanceof String && ((String) object).isEmpty()) {
            displayErrorAlert(message);
            return true;
        }
        return false;
    }

    private void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Configuration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
