package view.controllers.components;

import controller.BaseController;
import controller.GroupController;
import controller.LocationController;
import controller.SubjectController;
import dto.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import util.TimeFormatterUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventPopupController {
    private GroupController groupController;
    private LocationController locationController;
    private SubjectController subjectController;

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
    @FXML
    private Button deleteButton;

    public void setUp(Event event, BaseController baseController) {
        this.event = event;
        this.groupController = baseController.getGroupController();
        this.locationController = baseController.getLocationController();
        this.subjectController = baseController.getSubjectController();
    }

    @FXML
    private void initialize() {
        startHBox.getChildren().add(startTimeField);
        endHBox.getChildren().add(endTimeField);

        eventComboBox.getItems().addAll("Class", "Assignment");
        scheduleComboBox.getItems().addAll("Myself", "Group");
        assignmentComboBox.getItems().addAll("Individual", "Group");

        // TODO: Remove these lines of dummy data, used for testing
//        subjectComboBox.getItems().addAll("Math", "Science", "English", "History", "Geography");
//        locationComboBox.getItems().addAll("Room 1", "Room 2", "Room 3", "Room 4", "Room 5");
//        groupComboBox.getItems().addAll("Group 1", "Group 2", "Group 3", "Group 4", "Group 5");

        eventComboBox.addEventHandler(ActionEvent.ACTION, event -> handleEventChange());
        scheduleComboBox.addEventHandler(ActionEvent.ACTION, event -> handleScheduleChange());

        Platform.runLater(() -> {
            // Fetch data from the database
            subjectComboBox.getItems().addAll(
                    subjectController.fetchSubjectsByUser().stream()
                            .map(SubjectDTO::name)
                            .toList()
            );
            locationComboBox.getItems().addAll(
                    locationController.fetchAllLocations().stream()
                            .map(LocationDTO::name)
                            .toList()
            );
            groupComboBox.getItems().addAll(
                    groupController.fetchGroupsByUser().stream()
                            .map(GroupDTO::name)
                            .toList()
            );

            if (event == null) {
                eventComboBox.setValue("Class");
                scheduleComboBox.setValue("Myself");
                deleteButton.setVisible(false);
                deleteButton.setManaged(false);
                return;
            }

            eventComboBox.setDisable(true);

            if (event instanceof TeachingSessionDTO teachingSession) {
                LocalDateTime startDateTime = teachingSession.startDate();
                LocalDateTime endDateTime = teachingSession.endDate();
                String location = teachingSession.locationName();
                String subject = teachingSession.subjectName();
                String description = teachingSession.description();

                eventComboBox.setValue("Class");
                startDatePicker.setValue(startDateTime.toLocalDate());
                startTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(startDateTime));
                endTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(endDateTime));
                locationComboBox.setValue(location);
                subjectComboBox.setValue(subject);
                descriptionTextArea.setText(description);
            } else if (event instanceof AssignmentDTO assignment) {
                LocalDateTime publishingDateTime = assignment.publishingDate();
                LocalDateTime deadlineDateTime = assignment.deadline();
                String assignmentName = assignment.assignmentName();
                String assignmentType = assignment.type();
                String subject = assignment.subjectName();
                String description = assignment.description();

                eventComboBox.setValue("Assignment");
                startDatePicker.setValue(publishingDateTime.toLocalDate());
                startTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(publishingDateTime));
                endDatePicker.setValue(deadlineDateTime.toLocalDate());
                endTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(deadlineDateTime));
                nameTextField.setText(assignmentName);
                assignmentComboBox.setValue(assignmentType);
                subjectComboBox.setValue(subject);
                descriptionTextArea.setText(description);
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
                endDatePicker.setValue(null);
                endDatePicker.setDisable(true);

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
                endDatePicker.setDisable(false);

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

        Event newEvent;

        LocalDate startDate = startDatePicker.getValue();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        LocalTime startLocalTime = TimeFormatterUtil.getTimeFromString(startTime);
        LocalTime endLocalTime = TimeFormatterUtil.getTimeFromString(endTime);

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
            // TODO: Call controller to save teaching session for myself
        } else {
            String group = (String) groupComboBox.getValue();
            // TODO: Call controller to save teaching session for group
        }
    }

    @FXML
    private void handleDeleteEvent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this event?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            // TODO: Call controller to delete event
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
