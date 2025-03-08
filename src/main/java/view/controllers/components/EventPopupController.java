package view.controllers.components;

import controller.*;
import dto.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import util.TimeFormatterUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventPopupController {
    private EventController eventController;
    private GroupController groupController;
    private LocationController locationController;
    private SubjectController subjectController;
    private TimetableController timetableController;

    private Event event;
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
        this.eventController = baseController.getEventController();
        this.groupController = baseController.getGroupController();
        this.locationController = baseController.getLocationController();
        this.subjectController = baseController.getSubjectController();
        this.timetableController = baseController.getTimetableController();
    }

    @FXML
    private void initialize() {
        startHBox.getChildren().add(startTimeField);
        endHBox.getChildren().add(endTimeField);

        eventComboBox.getItems().addAll("Class", "Assignment");
        scheduleComboBox.getItems().addAll("Myself", "Group");
        assignmentComboBox.getItems().addAll("Individual", "Group");

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
            scheduleComboBox.setDisable(true);
            groupComboBox.setDisable(true);

            if (event instanceof TeachingSessionDTO teachingSession) {
                LocalDateTime startDateTime = teachingSession.startDate();
                LocalDateTime endDateTime = teachingSession.endDate();
                String location = teachingSession.locationName();
                String subject = teachingSession.subjectCode();
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
                String subject = assignment.subjectCode();
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
                toggleEventView(false);
                break;
            case "Assignment":
                toggleEventView(true);
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
                toggleScheduleView(false);
                break;
            case "Group":
                toggleScheduleView(true);
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

        Long id;
        if (event != null && event instanceof TeachingSessionDTO teachingSession) {
            id = teachingSession.id();
        } else if (event != null && event instanceof AssignmentDTO assignment) {
            id = assignment.id();
        } else {
            id = null;
        }

        Event newEvent = null;

        LocalDate startDate = startDatePicker.getValue();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();

        LocalTime startLocalTime = TimeFormatterUtil.getTimeFromString(startTime);
        LocalTime endLocalTime = TimeFormatterUtil.getTimeFromString(endTime);

        String scheduleFor = (String) scheduleComboBox.getValue();
        String subject = (String) subjectComboBox.getValue();
        String description = descriptionTextArea.getText();

        Long timetableId;

        if (scheduleFor.equals("Myself")) {
            timetableId = timetableController.fetchTimetableForUser();
        } else {
            String groupName = (String) groupComboBox.getValue();

            if (checkNullOrEmpty(groupName, "Please select a group")) {
                return;
            }
            if (!groupController.isUserGroupOwner(groupName)) {
                if (event == null) {
                    displayErrorAlert("Permission Error", "You do not have permission to add events for this group");
                } else {
                    displayErrorAlert("Permission Error", "You do not have permission to update events for this group");
                }
                return;
            }

            timetableId = timetableController.fetchTimetableForGroup(groupName);
        }

        if (timetableId == null) {
            System.out.println("Timetable ID not found");
            return;
        }

        switch (eventType) {
            case "Class":
                LocalDateTime startDateTime = LocalDateTime.of(startDate, startLocalTime);
                LocalDateTime endDateTime = LocalDateTime.of(startDate, endLocalTime);

                String location = (String) locationComboBox.getValue();

                newEvent = new TeachingSessionDTO(id, startDateTime, endDateTime, location, subject, description, timetableId);
                break;
            case "Assignment":
                LocalDate endDate = endDatePicker.getValue();
                LocalDateTime publishingDateTime = LocalDateTime.of(startDate, startLocalTime);
                LocalDateTime deadlineDateTime = LocalDateTime.of(endDate, endLocalTime);

                String assignmentName = nameTextField.getText();
                String assignmentType = (String) assignmentComboBox.getValue();

                newEvent = new AssignmentDTO(id, assignmentType, publishingDateTime, deadlineDateTime, assignmentName, subject, description, timetableId);
                break;
        }

        if (newEvent == null) {
            displayErrorAlert("Configuration Error", "Event type not recognised");
            return;
        }

        if (event == null) {
            eventController.addEvent(newEvent);
        } else {
            eventController.updateEvent(newEvent);
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
            if (scheduleComboBox.getValue() == "Myself") {
                eventController.deleteEvent(event);
            } else {
                String groupName = (String) groupComboBox.getValue();

                if (!groupController.isUserGroupOwner(groupName)) {
                    displayErrorAlert("Permission Error", "You do not have permission to delete events for this group");
                    return;
                }
                eventController.deleteEvent(event);
            }
        }
    }

    private boolean checkNullOrEmpty(Object object, String message) {
        if (object == null || object instanceof String && ((String) object).isEmpty()) {
            displayErrorAlert("Configuration Error", message);
            return true;
        }
        return false;
    }

    private void displayErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void toggleEventView(boolean isAssignment) {
        if (isAssignment) {
            startLabel.setText("Publishing Date");
            endLabel.setText("Due Date");
        } else {
            startLabel.setText("Start Time");
            endLabel.setText("End Time");
        }

        endDatePicker.setDisable(!isAssignment);

        for (Node child : popupGridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            if (rowIndex == null) {
                rowIndex = 0;
            }
            if (rowIndex == 6 || rowIndex == 7) {
                child.setVisible(isAssignment);
                child.setManaged(isAssignment);
            }
            if (rowIndex == 8) {
                child.setVisible(!isAssignment);
                child.setManaged(!isAssignment);
            }
        }
    }

    private void toggleScheduleView(boolean isGroup) {
        for (Node child : popupGridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            if (rowIndex == null) {
                rowIndex = 0;
            }
            if (rowIndex == 4) {
                child.setVisible(isGroup);
                child.setManaged(isGroup);
            }
        }
    }
}
