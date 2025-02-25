package view.controllers.pages.main;

import controller.Controller;
import dto.Event;
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
import view.controllers.components.EventPopupController;
import view.controllers.components.HeaderLabel;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Objects;

public class TimetableController implements ControllerAware {
    private static final int NUMBER_OF_BUTTONS = 2;
    Controller controller;

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
        });
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
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

        // Date formatting needs to be updated
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

    private void handleEditEvent() {

    }

    private void openEventPopup(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/components/timetable/event-popup.fxml"));
            Parent content = loader.load();

            EventPopupController popupController = loader.getController();
            popupController.setUp(event);

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
            int monthDay = startDate.plusDays(i).getDayOfMonth();
            int month = startDate.plusDays(i).getMonthValue();

            HeaderLabel header = new HeaderLabel(StringUtil.capitaliseFirst(weekDay), monthDay, month);
            timetableGrid.add(header, i, 0);

            GridPane.setHalignment(header, javafx.geometry.HPos.CENTER);
            GridPane.setValignment(header, javafx.geometry.VPos.CENTER);
        }
    }

    private String formatNumber(int number) {
        return String.format("%02d", number);
    }

    // Not to be implemented yet
    private void addColumn() {

    }

    // If number of columns is odd remove last, otherwise remove first
    // Not to be implemented yet
    private void removeColumn() {

    }

    // Fetch data for an entire week
    private void loadTimetable() {

    }
}
