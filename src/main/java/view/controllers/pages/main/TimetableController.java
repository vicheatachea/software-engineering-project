package view.controllers.pages.main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;

public class TimetableController {
    private static final int NUMBER_OF_BUTTONS = 2;

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
        Platform.runLater(() -> datePicker.setValue(LocalDate.now()));
    }

    private void addButtons() {
        try {
            Button[] buttons = new Button[NUMBER_OF_BUTTONS];
            for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
                buttons[i] = FXMLLoader.load(getClass().getResource("/layouts/components/timetable/timetable-button.fxml"));
            }

            buttons[0].setText("New Event");
            buttons[1].setText("\uD83D\uDD27");

            buttons[0].setOnAction(event -> handleAddEvent());
            // Settings buttons is not yet implemented

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
        dateLabel.setText(startDate + " - " + endDate);
        weekLabel.setText("Week " + currentWeek);
    }

    private void handleAddEvent() {
        // Get the start and end times, type, etc
        // Based on the start and end times allocate the necessary amount of cells
        // Change margins so that the times match the hours in relation to cell size
        // Maybe make a separate label component for this
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
