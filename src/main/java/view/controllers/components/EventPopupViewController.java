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
import javafx.stage.Stage;
import util.TimeFormatterUtil;
import view.controllers.pages.main.TimetableViewController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPopupViewController {
    private static final Logger logger = LoggerFactory.getLogger(EventPopupViewController.class);
    private static final String ERROR_TITLE = "error.title";
    private static final String EVENT_MYSELF = "event.myself";
    private static final String EVENT_GROUP = "event.group";
    private static final String EVENT_CLASS = "event.class";
    private static final String EVENT_ASSIGNMENT = "event.assignment";

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
            groupLabel.setText(viewText.getString(EVENT_GROUP));
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
                    viewText.getString(EVENT_CLASS),
                    viewText.getString(EVENT_ASSIGNMENT)
            );
            scheduleComboBox.getItems().addAll(viewText.getString(EVENT_MYSELF),
                    viewText.getString(EVENT_GROUP)
            );
            assignmentComboBox.getItems().addAll(
                    viewText.getString("event.individual"),
                    viewText.getString(EVENT_GROUP)
            );

            Locale currentLocale = localeController.getUserLocale();
            List<Locale> locales = localeController.getAvailableLocales();
            locales.forEach(locale ->
                    languageComboBox.getItems().add(locale.getDisplayLanguage(currentLocale))
            );

            eventComboBox.addEventHandler(ActionEvent.ACTION, actionEvent -> handleEventChange());
            scheduleComboBox.addEventHandler(ActionEvent.ACTION, actionEvent -> handleScheduleChange());
            groupComboBox.addEventHandler(ActionEvent.ACTION, actionEvent -> handleGroupChange());

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
                eventComboBox.setValue(viewText.getString(EVENT_CLASS));
                scheduleComboBox.setValue(viewText.getString(EVENT_MYSELF));
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
                Locale eventLocale = Locale.forLanguageTag(teachingSession.localeCode());
                String description = teachingSession.description();

                eventComboBox.setValue(viewText.getString(EVENT_CLASS));
                startDatePicker.setValue(startDateTime.toLocalDate());
                startTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(startDateTime));
                endTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(endDateTime));
                locationComboBox.setValue(location);
                languageComboBox.setValue(eventLocale.getDisplayLanguage(currentLocale));
                descriptionTextArea.setText(description);

                if (teachingSession.timetableId() == timetableController.fetchTimetableForUser()) {
                    scheduleComboBox.setValue(viewText.getString(EVENT_MYSELF));
                } else {
                    scheduleComboBox.setValue(viewText.getString(EVENT_GROUP));
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
                Locale eventLocale = Locale.forLanguageTag(assignment.localeCode());
                String description = assignment.description();

                eventComboBox.setValue(viewText.getString(EVENT_ASSIGNMENT));
                startDatePicker.setValue(publishingDateTime.toLocalDate());
                startTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(publishingDateTime));
                endDatePicker.setValue(deadlineDateTime.toLocalDate());
                endTimeField.setText(TimeFormatterUtil.getTimeFromDateTime(deadlineDateTime));
                nameTextField.setText(assignmentName);
                assignmentComboBox.setValue(assignmentType);
                languageComboBox.setValue(eventLocale.getDisplayLanguage(currentLocale));
                descriptionTextArea.setText(description);

                if (assignment.timetableId() == timetableController.fetchTimetableForUser()) {
                    scheduleComboBox.setValue(viewText.getString(EVENT_MYSELF));
                } else {
                    scheduleComboBox.setValue(viewText.getString(EVENT_GROUP));
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

        String classText = viewText.getString(EVENT_CLASS);
        String assignmentText = viewText.getString(EVENT_ASSIGNMENT);

        switch (eventType) {
            case String value when value.equals(classText) -> toggleEventView(false);
            case String value when value.equals(assignmentText) -> toggleEventView(true);
            default -> logger.info("Event type not recognised");
        }
    }

    private void handleScheduleChange() {
        String scheduleType = scheduleComboBox.getValue();

        if (scheduleType == null) {
            return;
        }

        String myselfText = viewText.getString(EVENT_MYSELF);
        String groupText = viewText.getString(EVENT_GROUP);

        switch (scheduleType) {
            case String value when value.equals(myselfText) -> toggleScheduleView(false);
            case String value when value.equals(groupText) -> toggleScheduleView(true);
            default -> logger.info("Schedule type not recognised");
        }
    }

    private void handleGroupChange() {
        String groupName = groupComboBox.getValue();

        if (groupName != null) {
            GroupDTO group = groupController.fetchGroupByName(groupName);
            subjectComboBox.setValue(group.subjectCode());
        }
    }

    @FXML
    private void handleSaveEvent() {
        if (!validateBasicFields()) {
            return;
        }
        if (!validateEventSpecificFields()) {
            return;
        }

        Event newEvent = createEvent();
        if (newEvent == null) {
            return;
        }

        if (event == null) {
            eventController.addEvent(newEvent);
        } else {
            eventController.updateEvent(newEvent);
        }
        leaveAndUpdate();
    }

    private boolean validateBasicFields() {
        return !checkNullOrEmpty(eventComboBox.getValue(), viewText.getString("event.promptEventType")) &&
                !checkNullOrEmpty(scheduleComboBox.getValue(), viewText.getString("event.promptScheduleType")) &&
                !checkNullOrEmpty(subjectComboBox.getValue(), viewText.getString("event.promptSubject")) &&
                !checkNullOrEmpty(languageComboBox.getValue(), viewText.getString("event.promptLanguage"));
    }

    private boolean validateEventSpecificFields() {
        String eventType = eventComboBox.getValue();

        if (eventType.equals(viewText.getString(EVENT_ASSIGNMENT))) {
            return !checkNullOrEmpty(startDatePicker.getValue(), viewText.getString("event.promptPublishingDate")) &&
                    !checkNullOrEmpty(endDatePicker.getValue(), viewText.getString("event.promptDueDate")) &&
                    !checkNullOrEmpty(nameTextField.getText(), viewText.getString("event.promptAssignmentName")) &&
                    !checkNullOrEmpty(assignmentComboBox.getValue(), viewText.getString("event.promptAssignmentType"));
        } else if (eventType.equals(viewText.getString(EVENT_CLASS))) {
            return !checkNullOrEmpty(startTimeField.getText(), viewText.getString("event.promptStartTime")) &&
                    !checkNullOrEmpty(endTimeField.getText(), viewText.getString("event.promptEndTime")) &&
                    !checkNullOrEmpty(locationComboBox.getValue(), viewText.getString("event.promptLocation"));
        }
        return false;
    }

    private Event createEvent() {
        Long id = extractEventId();
        Long timetableId = resolveTimetableId();
        if (timetableId == null) {
            return null;
        }

        Locale eventLocale = resolveEventLocale();
        if (eventLocale == null) {
            displayErrorAlert(
                    viewText.getString(ERROR_TITLE),
                    viewText.getString("error.event.invalidLanguage")
            );
            return null;
        }

        String eventType = eventComboBox.getValue();
        if (eventType.equals(viewText.getString(EVENT_CLASS))) {
            return createTeachingSession(id, timetableId, eventLocale);
        } else if (eventType.equals(viewText.getString(EVENT_ASSIGNMENT))) {
            return createAssignment(id, timetableId, eventLocale);
        }

        displayErrorAlert(
                viewText.getString(ERROR_TITLE),
                viewText.getString("error.event.notRecognised")
        );
        return null;
    }

    private Long extractEventId() {
        if (event instanceof TeachingSessionDTO teachingSession) {
            return teachingSession.id();
        } else if (event instanceof AssignmentDTO assignment) {
            return assignment.id();
        }
        return null;
    }

    private Long resolveTimetableId() {
        String scheduleFor = scheduleComboBox.getValue();
        if (scheduleFor.equals(viewText.getString(EVENT_MYSELF))) {
            return timetableController.fetchTimetableForUser();
        }

        String groupName = groupComboBox.getValue();
        if (checkNullOrEmpty(groupName, viewText.getString("event.promptGroup"))) {
            return null;
        }
        if (!groupController.isUserGroupOwner(groupName)) {
            displayErrorAlert(
                    viewText.getString(ERROR_TITLE),
                    viewText.getString("error.event.permission")
            );
            return null;
        }
        return timetableController.fetchTimetableForGroup(groupName);
    }

    private Locale resolveEventLocale() {
        String language = languageComboBox.getValue();
        Locale currentLocale = localeController.getUserLocale();

        for (Locale locale : localeController.getAvailableLocales()) {
            if (locale.getDisplayLanguage(currentLocale).equals(language)) {
                return locale;
            }
        }

        displayErrorAlert(
                viewText.getString(ERROR_TITLE),
                viewText.getString("error.event.invalidLanguage")
        );
        return null;
    }

    private TeachingSessionDTO createTeachingSession(Long id, Long timetableId, Locale eventLocale) {
        LocalTime startTime = TimeFormatterUtil.getTimeFromString(startTimeField.getText());
        LocalTime endTime = TimeFormatterUtil.getTimeFromString(endTimeField.getText());

        if (startTime.isAfter(endTime)) {
            displayErrorAlert(
                    viewText.getString(ERROR_TITLE),
                    viewText.getString("error.event.timeAfter")
            );
            return null;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(startDate, endTime);

        return new TeachingSessionDTO(
                id,
                startDateTime,
                endDateTime,
                locationComboBox.getValue(),
                subjectComboBox.getValue(),
                descriptionTextArea.getText(),
                timetableId,
                eventLocale.toLanguageTag()
        );
    }

    private AssignmentDTO createAssignment(Long id, Long timetableId, Locale eventLocale) {
        LocalTime startTime = TimeFormatterUtil.getTimeFromString(startTimeField.getText());
        LocalTime endTime = TimeFormatterUtil.getTimeFromString(endTimeField.getText());

        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        LocalDateTime publishingDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime deadlineDateTime = LocalDateTime.of(endDate, endTime);

        return new AssignmentDTO(
                id,
                assignmentComboBox.getValue(),
                publishingDateTime,
                deadlineDateTime,
                nameTextField.getText(),
                subjectComboBox.getValue(),
                descriptionTextArea.getText(),
                timetableId,
                eventLocale.toLanguageTag()
        );
    }

    @FXML
    private void handleDeleteEvent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(viewText.getString("confirmation.event.delete"));
        alert.setHeaderText(null);
        alert.setContentText(viewText.getString("confirmation.event.deletePrompt"));
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            if (Objects.equals(scheduleComboBox.getValue(), viewText.getString(EVENT_MYSELF))) {
                eventController.deleteEvent(event);
            } else {
                String groupName = groupComboBox.getValue();

                if (!groupController.isUserGroupOwner(groupName)) {
                    displayErrorAlert(
                            viewText.getString(ERROR_TITLE),
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
        if (object == null || object instanceof String string && string.isEmpty()) {
            displayErrorAlert(viewText.getString(ERROR_TITLE), message);
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
