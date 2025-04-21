package view.controllers.components;

import controller.BaseController;
import controller.EventController;
import controller.GroupController;
import controller.LocaleController;
import controller.LocationController;
import controller.SubjectController;
import controller.TimetableController;
import dto.AssignmentDTO;
import dto.Event;
import dto.GroupDTO;
import dto.LocationDTO;
import dto.SubjectDTO;
import dto.TeachingSessionDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import util.TimeFormatterUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import view.controllers.pages.main.TimetableViewController;


public class EventPopupViewController {
    private ResourceBundle viewText;
    private EventController eventController;
    private GroupController groupController;
    private LocaleController localeController;
    private LocationController locationController;
    private SubjectController subjectController;
    private TimetableController timetableController;
    private TimetableViewController timetableViewController;

    private Event event;
    private final TimeTextField startTimeField = new TimeTextField();
    private final TimeTextField endTimeField = new TimeTextField();

    @FXML
    private GridPane popupGridPane;
    @FXML
    private ComboBox<String> eventComboBox;
    @FXML
    private ComboBox<String> scheduleComboBox;
    @FXML
    private ComboBox<String> groupComboBox;
    @FXML
    private ComboBox<String> subjectComboBox;
    @FXML
    private ComboBox<String> assignmentComboBox;
    @FXML
    private ComboBox<String> locationComboBox;
    @FXML
    private TextField nameTextField;
    @FXML
    private HBox startHBox;
    @FXML
    private HBox endHBox;
    @FXML
    private Label eventLabel;
    @FXML
    private Label startLabel;
    @FXML
    private Label endLabel;
    @FXML
    private Label scheduleLabel;
    @FXML
    private Label groupLabel;
    @FXML
    private Label subjectLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label assignmentLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;

    public void setUp(Event event, BaseController baseController, TimetableViewController timetableViewController) {
        this.event = event;
        this.eventController = baseController.getEventController();
        this.groupController = baseController.getGroupController();
        this.localeController = baseController.getLocaleController();
        this.locationController = baseController.getLocationController();
        this.subjectController = baseController.getSubjectController();
        this.timetableController = baseController.getTimetableController();
        this.timetableViewController = timetableViewController;
        this.viewText = baseController.getLocaleController().getUIBundle();
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            eventLabel.setText(viewText.getString("event.eventType"));
            startLabel.setText(viewText.getString("event.startTime"));
            endLabel.setText(viewText.getString("event.endTime"));
            scheduleLabel.setText(viewText.getString("event.scheduleType"));
            groupLabel.setText(viewText.getString("event.group"));
            subjectLabel.setText(viewText.getString("event.subject"));
            nameLabel.setText(viewText.getString("event.assignmentName"));
            assignmentLabel.setText(viewText.getString("event.assignmentType"));
            locationLabel.setText(viewText.getString("event.location"));
            languageLabel.setText(viewText.getString("event.language"));
            descriptionLabel.setText(viewText.getString("event.description"));

            saveButton.setText(viewText.getString("event.saveEvent"));
            deleteButton.setText(viewText.getString("event.deleteEvent"));

            startHBox.getChildren().add(startTimeField);
            endHBox.getChildren().add(endTimeField);

            eventComboBox.getItems().addAll(
                    viewText.getString("event.class"),
                    viewText.getString("event.assignment")
            );
            scheduleComboBox.getItems().addAll(viewText.getString("event.myself"),
                    viewText.getString("event.group")
            );
            assignmentComboBox.getItems().addAll(
                    viewText.getString("event.individual"),
                    viewText.getString("event.group")
            );

            Locale currentLocale = localeController.getUserLocale();
            List<Locale> locales = localeController.getAvailableLocales();
            locales.forEach(locale ->
                    languageComboBox.getItems().add(locale.getDisplayLanguage(currentLocale))
            );

            eventComboBox.addEventHandler(ActionEvent.ACTION, event -> handleEventChange());
            scheduleComboBox.addEventHandler(ActionEvent.ACTION, event -> handleScheduleChange());
            groupComboBox.addEventHandler(ActionEvent.ACTION, event -> handleGroupChange());

            // Fetch data from the database
            subjectComboBox.getItems().addAll(
                    subjectController.fetchSubjectsByUser().stream()
                            .map(SubjectDTO::code)
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
                eventComboBox.setValue(viewText.getString("event.class"));
                scheduleComboBox.setValue(viewText.getString("event.myself"));
                Locale locale = localeController.getUserLocale();
                languageComboBox.setValue(locale.getDisplayLanguage(locale));
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

                eventComboBox.setValue(viewText.getString("event.class"));
                startDatePicker.setValue(startDateTime.toLocalDate());
                startTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(startDateTime));
                endTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(endDateTime));
                locationComboBox.setValue(location);
                descriptionTextArea.setText(description);

                if (teachingSession.timetableId() == timetableController.fetchTimetableForUser()) {
                    scheduleComboBox.setValue(viewText.getString("event.myself"));
                } else {
                    scheduleComboBox.setValue(viewText.getString("event.group"));
                    groupComboBox.setValue(groupController.fetchGroupByTimetableId(teachingSession.timetableId()).name());
                    subjectComboBox.setDisable(true);
                }
                subjectComboBox.setValue(subject);
            } else if (event instanceof AssignmentDTO assignment) {
                LocalDateTime publishingDateTime = assignment.publishingDate();
                LocalDateTime deadlineDateTime = assignment.deadline();
                String assignmentName = assignment.assignmentName();
                String assignmentType = assignment.type();
                String subject = assignment.subjectCode();
                String description = assignment.description();

                eventComboBox.setValue(viewText.getString("event.assignment"));
                startDatePicker.setValue(publishingDateTime.toLocalDate());
                startTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(publishingDateTime));
                endDatePicker.setValue(deadlineDateTime.toLocalDate());
                endTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(deadlineDateTime));
                nameTextField.setText(assignmentName);
                assignmentComboBox.setValue(assignmentType);
                descriptionTextArea.setText(description);

                if (assignment.timetableId() == timetableController.fetchTimetableForUser()) {
                    scheduleComboBox.setValue(viewText.getString("event.myself"));
                } else {
                    scheduleComboBox.setValue(viewText.getString("event.group"));
                    groupComboBox.setValue(groupController.fetchGroupByTimetableId(assignment.timetableId()).name());
                    subjectComboBox.setDisable(true);
                }
                subjectComboBox.setValue(subject);
            }
        });
    }

    private void handleEventChange() {
        String eventType = eventComboBox.getValue();

        if (eventType == null) {
            return;
        }

        String classText = viewText.getString("event.class");
        String assignmentText = viewText.getString("event.assignment");

        switch (eventType) {
            case String value when value.equals(classText) -> toggleEventView(false);
            case String value when value.equals(assignmentText) -> toggleEventView(true);
            default -> System.out.println("Event type not recognised");
        }
    }

    private void handleScheduleChange() {
        String scheduleType = scheduleComboBox.getValue();

        if (scheduleType == null) {
            return;
        }

        String myselfText = viewText.getString("event.myself");
        String groupText = viewText.getString("event.group");

        switch (scheduleType) {
            case String value when value.equals(myselfText)
                    -> toggleScheduleView(false);
            case String value when value.equals(groupText)
                    -> toggleScheduleView(true);
            default
                    -> System.out.println("Schedule type not recognised");
        }
    }

    private void handleGroupChange() {
        String groupName = groupComboBox.getValue();

        if (groupName != null) {
            GroupDTO group = groupController
                    .fetchGroupByName(groupName);
            subjectComboBox.setValue(
                    group.subjectCode()
            );
        }
    }

    @FXML
    private void handleSaveEvent() {
        String eventType = eventComboBox.getValue();

        if (checkNullOrEmpty(eventType, viewText.getString("event.promptEventType")) ||
                checkNullOrEmpty(scheduleComboBox.getValue(), viewText.getString("event.promptScheduleType")) ||
                checkNullOrEmpty(subjectComboBox.getValue(), viewText.getString("event.promptSubject")) ||
                checkNullOrEmpty(languageComboBox.getValue(), viewText.getString("event.promptLanguage"))) {
            return;
        }

        if (eventType.equals(viewText.getString("event.assignment"))) {
            if (checkNullOrEmpty(startDatePicker.getValue(), viewText.getString("event.promptPublishingDate")) ||
                    checkNullOrEmpty(endDatePicker.getValue(), viewText.getString("event.promptDueDate")) ||
                    checkNullOrEmpty(nameTextField.getText(), viewText.getString("event.promptAssignmentName")) ||
                    checkNullOrEmpty(assignmentComboBox.getValue(), viewText.getString("event.promptAssignmentType"))) {
                return;
            }
        }

        if (eventType.equals(viewText.getString("event.class"))) {
            if (checkNullOrEmpty(startTimeField.getText(), viewText.getString("event.promptStartTime")) ||
                    checkNullOrEmpty(endTimeField.getText(), viewText.getString("event.promptEndTime")) ||
                    checkNullOrEmpty(locationComboBox.getValue(), viewText.getString("event.promptLocation"))) {
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


        LocalTime startLocalTime;
        LocalTime endLocalTime;

        try {
            startLocalTime = TimeFormatterUtil.getTimeFromString(startTime);
            endLocalTime = TimeFormatterUtil.getTimeFromString(endTime);
        } catch (Exception e) {
            displayErrorAlert(
                    viewText.getString("error.title"),
                    viewText.getString("error.invalidTime")
            );
            return;
        }

        String scheduleFor = scheduleComboBox.getValue();
        String subject = subjectComboBox.getValue();
        String language = languageComboBox.getValue();
        String description = descriptionTextArea.getText();

        Locale currentLocale = localeController.getUserLocale();
        Locale eventLocale = null;

        for (Locale locale : localeController.getAvailableLocales()) {
            if (locale.getDisplayLanguage(currentLocale).equals(language)) {
                eventLocale = locale;
                break;
            }
        }

        if (eventLocale == null) {
            displayErrorAlert(
                    viewText.getString("error.title"),
                    viewText.getString("error.event.invalidLanguage")
            );
        }

        Long timetableId;

        if (scheduleFor.equals(viewText.getString("event.myself"))) {
            timetableId = timetableController.fetchTimetableForUser();
        } else {
            String groupName = groupComboBox.getValue();

            if (checkNullOrEmpty(groupName, viewText.getString("event.promptGroup"))) {
                return;
            }
            if (!groupController.isUserGroupOwner(groupName)) {
                displayErrorAlert(
                        viewText.getString("error.title"),
                        viewText.getString("error.event.permission")
                );
                return;
            }

            timetableId = timetableController.fetchTimetableForGroup(groupName);
        }

        switch (eventType) {
            case String value when value.equals(viewText.getString("event.class")) -> {
                if (startLocalTime.isAfter(endLocalTime)) {
                    displayErrorAlert(
                            viewText.getString("error.title"),
                            viewText.getString("error.event.timeAfter")
                    );
                    return;
                }

                LocalDateTime startDateTime = LocalDateTime.of(startDate, startLocalTime);
                LocalDateTime endDateTime = LocalDateTime.of(startDate, endLocalTime);

                String location = locationComboBox.getValue();

                newEvent = new TeachingSessionDTO(id, startDateTime, endDateTime, location, subject, description, timetableId, eventLocale.toLanguageTag());
            }
            case String value when value.equals(viewText.getString("event.assignment")) -> {
                LocalDate endDate = endDatePicker.getValue();
                LocalDateTime publishingDateTime = LocalDateTime.of(startDate, startLocalTime);
                LocalDateTime deadlineDateTime = LocalDateTime.of(endDate, endLocalTime);

                String assignmentName = nameTextField.getText();
                String assignmentType = assignmentComboBox.getValue();

                newEvent = new AssignmentDTO(id, assignmentType, publishingDateTime, deadlineDateTime, assignmentName, subject, description, timetableId, eventLocale.toLanguageTag());
            }
            default -> System.out.println("Event type not recognised");
        }

        if (newEvent == null) {
            displayErrorAlert(
                    viewText.getString("error.title"),
                    viewText.getString("error.event.notRecognised")
            );
            return;
        }

        if (event == null) {
            eventController.addEvent(newEvent);
        } else {
            eventController.updateEvent(newEvent);
        }
        leaveAndUpdate();
    }

    @FXML
    private void handleDeleteEvent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(viewText.getString("confirmation.event.delete"));
        alert.setHeaderText(null);
        alert.setContentText(viewText.getString("confirmation.event.deletePrompt"));
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            if (Objects.equals(scheduleComboBox.getValue(), viewText.getString("event.myself"))) {
                eventController.deleteEvent(event);
            } else {
                String groupName = groupComboBox.getValue();

                if (!groupController.isUserGroupOwner(groupName)) {
                    displayErrorAlert(
                            viewText.getString("error.title"),
                            viewText.getString("error.event.permission")
                    );
                    return;
                }
                eventController.deleteEvent(event);
            }
            leaveAndUpdate();
        }
    }

    private boolean checkNullOrEmpty(Object object, String message) {
        if (object == null || object instanceof String && ((String) object).isEmpty()) {
            displayErrorAlert(viewText.getString("error.title"), message);
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
            startLabel.setText(viewText.getString("event.publishingDate"));
            endLabel.setText(viewText.getString("event.dueDate"));
        } else {
            startLabel.setText(viewText.getString("event.startTime"));
            endLabel.setText(viewText.getString("event.endTime"));
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
        adjustStageSize();
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
        subjectComboBox.setValue(null);
        subjectComboBox.setDisable(isGroup);
        adjustStageSize();
    }

    private void adjustStageSize() {
        Stage stage = (Stage) popupGridPane.getScene().getWindow();
        stage.sizeToScene();
    }

    private void leaveAndUpdate() {
        timetableViewController.loadTimetable();
        Stage stage = (Stage) popupGridPane.getScene().getWindow();
        stage.close();
    }
}
