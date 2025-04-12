package view.controllers.pages.main;

import controller.BaseController;
import controller.EventController;
import controller.LocaleController;
import controller.UserController;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.controllers.ControllerAware;
import view.controllers.components.EventLabel;
import view.controllers.components.EventPopupViewController;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TimetableViewController implements ControllerAware {
    private ResourceBundle viewText;
    private static final int NUMBER_OF_BUTTONS = 2;
    BaseController baseController;
    EventController eventController;
    LocaleController localeController;
    UserController userController;
    LocalDateTime startDate;
    LocalDateTime endDate;
    int currentWeek;

    private double cellHeight;
    private double cellWidth;

    @FXML
    private HBox topbar;
    @FXML
    private GridPane timetableGrid;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private Label dateLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label weekLabel;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            addButtons();

            languageComboBox.getItems().add(viewText.getString("timetable.allLanguages"));
            List<Locale> locales = localeController.getAvailableLocales();
            locales.forEach(locale ->
                    languageComboBox.getItems().add(locale.getDisplayLanguage(locale))
            );
            languageComboBox.setValue(viewText.getString("timetable.allLanguages"));
            languageComboBox.addEventHandler(ActionEvent.ACTION, event -> loadTimetable());

            datePicker.setValue(LocalDate.now());
            Locale.setDefault(baseController.getLocaleController().getUserLocale());
            handleDatePick();

            updateTimetableHeaders();
            updateTimetableHours();
            loadTimetable();

            timetableGrid.heightProperty().addListener((observable, oldValue, newValue) -> updateEventHeight());
            timetableGrid.widthProperty().addListener((observable, oldValue, newValue) -> updateEventWidth());
        });
    }

    @Override
    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
        this.eventController = baseController.getEventController();
        this.localeController = baseController.getLocaleController();
        this.userController = baseController.getUserController();
        this.viewText = baseController.getLocaleController().getUIBundle();
    }

    private void addButtons() {
        try {
            Button[] buttons = new Button[NUMBER_OF_BUTTONS];
            for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
                buttons[i] = FXMLLoader.load(getClass().getResource("/layouts/components/timetable/timetable-button.fxml"));
            }

            buttons[0].setText(viewText.getString("timetable.newEvent"));
            buttons[1].setText("\uD83D\uDD27");

            buttons[0].setOnAction(event -> handleNewEvent());
            // Settings buttons is not yet implemented
            buttons[1].setDisable(true);

            topbar.getChildren().addFirst(buttons[0]);
            topbar.getChildren().add(buttons[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDatePick() {
        LocalDate newDate = datePicker.getValue();

        if (newDate == null) {
            return;
        }

        LocalDateTime previousStartDate = startDate;
        startDate = newDate.with(DayOfWeek.MONDAY).atStartOfDay();
        endDate = newDate.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
        currentWeek = newDate.get(WeekFields.ISO.weekOfWeekBasedYear());

        String startDay = formatNumber(startDate.getDayOfMonth());
        String endDay = formatNumber(endDate.getDayOfMonth());
        String startMonth = startDate.getMonth().toString().toLowerCase();
        String endMonth = endDate.getMonth().toString().toLowerCase();
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();

        Map<String, Object> values = new HashMap<>();
        values.put("startDay", startDay);
        values.put("endDay", endDay);
        values.put("startMonth", viewText.getString("timetable.shortMonth." + startMonth));
        values.put("endMonth", viewText.getString("timetable.shortMonth." + endMonth));
        values.put("startYear", startYear);
        values.put("endYear", endYear);

        if (startYear != endYear) {
            dateLabel.setText(implementPattern(viewText.getString("timetable.longDate"), values));
        } else if (!Objects.equals(startMonth, endMonth)) {
            dateLabel.setText(implementPattern(viewText.getString("timetable.mediumDate"), values));
        } else {
            dateLabel.setText(implementPattern(viewText.getString("timetable.shortDate"), values));
        }

        weekLabel.setText(MessageFormat.format(viewText.getString("timetable.week"), currentWeek));

        if (!Objects.equals(previousStartDate, startDate)) {
            updateTimetableHeaders();
            loadTimetable();
        }
    }


    private String implementPattern(String pattern, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            pattern = pattern.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return pattern;
    }

    private void handleNewEvent() {
        if (!userController.isUserLoggedIn()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString("error.title"));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("error.event.loggedIn"));
            alert.showAndWait();
            return;
        }
        openEventPopup(null);
    }

    private void handleEditEvent(Event event) {
        openEventPopup(event);
    }

    private void openEventPopup(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/components/timetable/event-popup.fxml"));
            Parent content = loader.load();

            EventPopupViewController popupViewController = loader.getController();
            popupViewController.setUp(event, baseController, this);

            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(content));

            popupStage.setTitle(event == null ? viewText.getString("timetable.newEvent") :
                    viewText.getString("timetable.editEvent"));
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(topbar.getScene().getWindow());
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTimetableHeaders() {
        timetableGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
                GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) > 0);
        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        for (int i = 0; i < daysBetween; i++) {

            String weekDay = startDate.plusDays(i).getDayOfWeek().toString().toLowerCase();
            String monthDay = formatNumber(startDate.plusDays(i).getDayOfMonth());
            String month = formatNumber(startDate.plusDays(i).getMonthValue());

            Map<String, Object> values = new HashMap<>();
            values.put("weekDay", viewText.getString("timetable.shortDay." + weekDay));
            values.put("monthDay", monthDay);
            values.put("month", month);

            Label header = new Label(implementPattern(viewText.getString("timetable.simpleDate"), values));
            timetableGrid.add(header, i + 1, 0);

            GridPane.setHalignment(header, javafx.geometry.HPos.CENTER);
            GridPane.setValignment(header, javafx.geometry.VPos.CENTER);
        }
    }

    private void updateTimetableHours() {
        int rowCount = timetableGrid.getRowCount();
        double timeStep = 24.0 / (rowCount - 1);

        for (int i = 0; i < rowCount - 1; i++) {
            int hours = (int) (i * timeStep) % 24;
            int minutes = (int) ((i * timeStep % 24 - hours) * 60);

            Label timeLabel = new Label(formatNumber(hours) + ":" + formatNumber(minutes));
            timeLabel.setTranslateY(8);
            timetableGrid.add(timeLabel, 0, i);

            timeLabel.getStyleClass().add("time-label");

            GridPane.setHalignment(timeLabel, javafx.geometry.HPos.CENTER);
            GridPane.setValignment(timeLabel, javafx.geometry.VPos.BOTTOM);
        }
    }

    private String formatNumber(int number) {
        return String.format("%02d", number);
    }

    public void loadTimetable() {
        if (!userController.isUserLoggedIn()) {
            return;
        }

        clearTimetable();
        String language = languageComboBox.getValue();
        List<Event> events;

        if (language.equals(viewText.getString("timetable.allLanguages"))) {
            events = eventController.fetchEventsByUser(startDate, endDate);
        } else {
            Locale selectedLocale = localeController.getAvailableLocales().stream()
                    .filter(locale -> locale.getDisplayLanguage(locale).equals(language))
                    .findFirst()
                    .orElse(null);
            if (selectedLocale != null) {
                events = eventController.fetchEventsByLocale(startDate, endDate, selectedLocale.toLanguageTag());
            } else {
                events = eventController.fetchEventsByUser(startDate, endDate);
            }
        }

        for (Event event : events) {
            int column, startRow, endRow;
            EventLabel eventLabel;

            if (event instanceof TeachingSessionDTO teachingSession) {
                LocalDateTime teachingSessionStart = teachingSession.startDate();
                LocalDateTime teachingSessionEnd = teachingSession.endDate();

                column = (int) ChronoUnit.DAYS.between(startDate, teachingSessionStart);
                startRow = getRowIndex(teachingSessionStart, true);
                endRow = getRowIndex(teachingSessionEnd, false);

                double[] sizeAndOffset = getEventHeightAndOffset(teachingSessionStart, teachingSessionEnd);

                eventLabel = new EventLabel(teachingSession, sizeAndOffset[0], sizeAndOffset[1]);
                eventLabel.getStyleClass().add("teaching-session-label");
            } else if (event instanceof AssignmentDTO assignment) {
                LocalDateTime deadline = assignment.deadline();
                LocalDateTime deadlineBuffer = deadline.minusHours(1);

                column = (int) ChronoUnit.DAYS.between(startDate, deadline);

                LocalDateTime startTime = deadlineBuffer;
                LocalDateTime endTime = deadline;

                if (deadline.getDayOfMonth() != deadlineBuffer.getDayOfMonth()) {
                    if (deadline.getHour() == 0 && deadline.getMinute() == 0) {
                        column -= 1;
                        endTime = LocalDateTime.of(deadlineBuffer.getYear(), deadlineBuffer.getMonth(), deadlineBuffer.getDayOfMonth(), 23, 59);
                    } else {
                        startTime = LocalDateTime.of(deadline.getYear(), deadline.getMonth(), deadline.getDayOfMonth(), 0, 0);
                    }
                }

                startRow = getRowIndex(startTime, true);
                endRow = getRowIndex(endTime, false);

                double[] sizeAndOffset = getEventHeightAndOffset(startTime, endTime);

                eventLabel = new EventLabel(assignment, sizeAndOffset[0], sizeAndOffset[1]);
                eventLabel.getStyleClass().add("assignment-label");
            } else {
                System.out.println("An event with an unknown type was found");
                continue;
            }

            column += 1;

            if (column < 1 || column > (timetableGrid.getColumnCount() - 1)) {
                System.out.println("An event with a date outside the timetable range was found");
                continue;
            }
            // This code is probably redundant
            if (startRow == -1 || endRow == -1) {
                System.out.println("An invalid event was found");
                continue;
            }

            Set<EventLabel> overlappingEvents = getEventLabelsInRange(column, startRow, endRow);

            if (!overlappingEvents.isEmpty()) {
                int maxLabelPosition = overlappingEvents.stream()
                        .mapToInt(EventLabel::getNumberOfLabels)
                        .max()
                        .orElse(1);

                AtomicInteger i = new AtomicInteger(1);
                boolean added = false;
                for (; i.get() <= maxLabelPosition; i.incrementAndGet()) {
                    if (overlappingEvents.stream().noneMatch(eventLabel1 -> eventLabel1.getLabelPosition() == i.get())) {
                        eventLabel.updateLabelPosition(i.get(), maxLabelPosition);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    eventLabel.updateLabelPosition(maxLabelPosition + 1, maxLabelPosition + 1);
                    overlappingEvents.forEach(eventLabel1 -> eventLabel1.updateLabelPosition(eventLabel1.getLabelPosition(), maxLabelPosition + 1));
                }
            }

            GridPane.setHalignment(eventLabel, javafx.geometry.HPos.LEFT);
            GridPane.setValignment(eventLabel, javafx.geometry.VPos.TOP);

            // Calling eventLabel.getEvent() instead of just event should avoid storing the event twice
            eventLabel.setOnMouseClicked(mouseEvent -> handleEditEvent(eventLabel.getEvent()));
            timetableGrid.add(eventLabel, column, startRow, 1, endRow - startRow + 1);
        }

        updateEventHeight();
        updateEventWidth();
    }

    private void clearTimetable() {
        timetableGrid.getChildren().removeIf(node -> {
            Integer column = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            return node instanceof Label && (column != null && column > 0) && (row != null && row > 0);
        });
    }

    private int getRowIndex(LocalDateTime dateTime, boolean isStart) {
        double timeStep = 24.0 / (timetableGrid.getRowCount() - 1);
        int hours = dateTime.getHour();
        int minutes = dateTime.getMinute();
        double timeInHours = hours + minutes / 60.0;
        return isStart ? (int) (timeInHours / timeStep + 1) : (int) Math.ceil(timeInHours / timeStep);
    }

    // First value is height, second is offset
    private double[] getEventHeightAndOffset(LocalDateTime start, LocalDateTime end) {
        double timeStep = 24.0 / (timetableGrid.getRowCount() - 1);
        double startHours = start.getHour() + start.getMinute() / 60.0;
        double endHours = end.getHour() + end.getMinute() / 60.0;

        double height = (endHours - startHours) / timeStep;
        double offset = (startHours % timeStep) / timeStep;

        return new double[]{height, offset};
    }

    private void updateEventHeight() {
        cellHeight = timetableGrid.getHeight() / timetableGrid.getRowCount();
        timetableGrid.getChildren().stream()
                .filter(node -> node instanceof EventLabel)
                .forEach(node -> ((EventLabel) node).updateLabelHeight(cellHeight));

        List<RowConstraints> rowConstraints = timetableGrid.getRowConstraints();
        for (RowConstraints constraint : rowConstraints) {
            constraint.setMinHeight(cellHeight);
            constraint.setPrefHeight(cellHeight);
            constraint.setMaxHeight(cellWidth);
        }
    }

    private void updateEventWidth() {
        // 50 is the width of the first column
        cellWidth = (timetableGrid.getWidth() - 50) / (timetableGrid.getColumnCount() - 1);
        timetableGrid.getChildren().stream()
                .filter(node -> node instanceof EventLabel)
                .forEach(node -> ((EventLabel) node).updateLabelWidth(cellWidth));

        List<ColumnConstraints> columnConstraints = timetableGrid.getColumnConstraints();
        for (int i = 1; i < columnConstraints.size(); i++) {
            ColumnConstraints constraint = columnConstraints.get(i);
            constraint.setMinWidth(cellWidth);
            constraint.setPrefWidth(cellWidth);
            constraint.setMaxWidth(cellWidth);
        }
    }

    private Set<EventLabel> getEventLabelsInRange(int column, int startRow, int endRow) {
        Set<EventLabel> uniqueEventLabels = new HashSet<>();

        for (Node node : timetableGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer rowSpan = GridPane.getRowSpan(node);

            if (colIndex != null && rowIndex != null && rowSpan != null) {
                int nodeEndRow = rowIndex + rowSpan - 1;

                if (colIndex == column &&
                        ((rowIndex >= startRow && rowIndex <= endRow) || (nodeEndRow >= startRow && nodeEndRow <= endRow) ||
                                (rowIndex <= startRow && nodeEndRow >= endRow))) {

                    if (node instanceof EventLabel eventLabel) {
                        uniqueEventLabels.add(eventLabel);
                    }
                }
            }
        }

        return uniqueEventLabels;
    }

    // Not to be implemented yet
    private void addColumn() {

    }

    // If number of columns is odd remove last, otherwise remove first
    // Not to be implemented yet
    private void removeColumn() {

    }
}
