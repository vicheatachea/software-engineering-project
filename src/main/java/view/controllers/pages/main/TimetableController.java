package view.controllers.pages.main;

import controller.BaseController;
import controller.EventController;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.StringUtil;
import view.controllers.ControllerAware;
import view.controllers.components.EventLabel;
import view.controllers.components.EventPopupController;
import view.controllers.components.HeaderLabel;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Objects;

public class TimetableController implements ControllerAware {
    private static final int NUMBER_OF_BUTTONS = 2;
    BaseController baseController;
    EventController eventController;

    LocalDate currentDate;
    LocalDate startDate;
    LocalDate endDate;
    int currentWeek;

    @FXML
    private HBox topbar;
    @FXML
    private GridPane timetableGrid;
    @FXML
    private Label dateLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label weekLabel;

    @FXML
    private void initialize() {
        addButtons();
        Platform.runLater(() -> {
            datePicker.setValue(LocalDate.now());
            handleDatePick();

            updateTimetableHeaders();
            updateTimetableHours();
            loadTimetable();
        });
    }

    @Override
    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
        this.eventController = baseController.getEventController();
    }

    private void addButtons() {
        try {
            Button[] buttons = new Button[NUMBER_OF_BUTTONS];
            for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
                buttons[i] = FXMLLoader.load(getClass().getResource("/layouts/components/timetable/timetable-button.fxml"));
            }

            buttons[0].setText("New Event");
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

        currentDate = newDate;
        startDate = newDate.with(DayOfWeek.MONDAY);
        endDate = newDate.with(DayOfWeek.SUNDAY);
        currentWeek = newDate.get(WeekFields.ISO.weekOfWeekBasedYear());

        String startDay = formatNumber(startDate.getDayOfMonth());
        String endDay = formatNumber(endDate.getDayOfMonth());
        String fullStartMonth = startDate.getMonth().toString();
        String startMonth = StringUtil.capitaliseFirst(startDate.getMonth().toString().substring(0, 3));
        String fullEndMonth = endDate.getMonth().toString();
        String endMonth = StringUtil.capitaliseFirst(endDate.getMonth().toString().substring(0, 3));
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();

        if (startYear != endYear) {
            dateLabel.setText(startDay + " " + startMonth + ". " + startYear + " - " + endDay + " " + endMonth + ". " + endYear);
        } else if (!Objects.equals(fullStartMonth, fullEndMonth)) {
            dateLabel.setText(startDay + " " + startMonth + ". - " + endDay + " " + endMonth + ". " + startYear);
        } else {
            dateLabel.setText(startDay + " - " + endDay + " " + startMonth + ". " + startYear);
        }

        weekLabel.setText("Week " + currentWeek);

        updateTimetableHeaders();
    }

    private void handleNewEvent() {
        openEventPopup(null);
    }

    private void handleEditEvent(Event event) {
        openEventPopup(event);
    }

    private void openEventPopup(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/components/timetable/event-popup.fxml"));
            Parent content = loader.load();

            EventPopupController popupController = loader.getController();
            popupController.setUp(event, baseController);

            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(content));

            popupStage.setTitle(event == null ? "New Event" : "Edit Event");
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(topbar.getScene().getWindow());
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTimetableHeaders() {
        timetableGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
                GridPane.getRowIndex(node) == 0);
        int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        for (int i = 0; i < daysBetween; i++) {

            String weekDay = startDate.plusDays(i).getDayOfWeek().toString().substring(0, 3);
            String monthDay = formatNumber(startDate.plusDays(i).getDayOfMonth());
            String month = formatNumber(startDate.plusDays(i).getMonthValue());

            HeaderLabel header = new HeaderLabel(StringUtil.capitaliseFirst(weekDay), monthDay, month);
            timetableGrid.add(header, i + 1, 0);

            GridPane.setHalignment(header, javafx.geometry.HPos.CENTER);
            GridPane.setValignment(header, javafx.geometry.VPos.CENTER);
        }
    }

    private void updateTimetableHours() {
        int rowCount = timetableGrid.getRowCount();
        double timeStep = 24.0 / (rowCount - 1);

        for (int i = 0; i < rowCount; i++) {
            int hours = (int) (i * timeStep);
            int minutes = (int) ((i * timeStep - hours) * 60);

            Label timeLabel = new Label(formatNumber(hours) + ":" + formatNumber(minutes));
            timetableGrid.add(timeLabel, 0, i);

            GridPane.setHalignment(timeLabel, javafx.geometry.HPos.CENTER);
            GridPane.setValignment(timeLabel, javafx.geometry.VPos.BOTTOM);
        }
    }

    private String formatNumber(int number) {
        return String.format("%02d", number);
    }

    private void loadTimetable() {
        List<Event> events = eventController.fetchEventsByUser(startDate, endDate);

        for (Event event : events) {
            int column = -1, startRow = -1, endRow = -1;
            EventLabel eventLabel = new EventLabel(event);

            if (event instanceof TeachingSessionDTO teachingSession) {
                column = (int) ChronoUnit.DAYS.between(startDate, teachingSession.startDate());
                startRow = getRowIndex(teachingSession.startDate(), true);
                endRow = getRowIndex(teachingSession.endDate(), false);

                eventLabel.getStyleClass().add("teaching-session-label");
            } else if (event instanceof AssignmentDTO assignment) {
                column = (int) ChronoUnit.DAYS.between(startDate, assignment.deadline());
                LocalDateTime deadline = assignment.deadline();
                startRow = getRowIndex(deadline.minusHours(1), true);
                endRow = getRowIndex(deadline, false);

                eventLabel.getStyleClass().add("assignment-label");
            }

            if (column == -1 || startRow == -1 || endRow == -1) {
                return;
            }

            // Calling eventLabel.getEvent() instead of just event should avoid storing the event twice
            eventLabel.setOnMouseClicked(mouseEvent -> handleEditEvent(eventLabel.getEvent()));
            timetableGrid.add(eventLabel, column, startRow, 1, endRow - startRow + 1);
        }
    }

    private int getRowIndex(LocalDateTime dateTime, boolean isStart) {
        double timeStep = 24.0 / (timetableGrid.getRowCount() - 1);
        int hours = dateTime.getHour();
        int minutes = dateTime.getMinute();
        double timeInHours = hours + minutes / 60.0;
        return isStart ? (int) (timeInHours / timeStep + 1) : (int) Math.ceil(timeInHours / timeStep);
    }

    // Not to be implemented yet
    private void addColumn() {

    }

    // If number of columns is odd remove last, otherwise remove first
    // Not to be implemented yet
    private void removeColumn() {

    }
}
