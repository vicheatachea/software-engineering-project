package view.controllers.pages.main;

import controller.Controller;
import dto.Event;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.controllers.ControllerAware;
import view.controllers.components.EventPopupController;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

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
        Platform.runLater(() -> datePicker.setValue(LocalDate.now()));
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
        dateLabel.setText(startDate + " - " + endDate);
        weekLabel.setText("Week " + currentWeek);
    }

    private void handleNewEvent() {
        openEventPopup(null);
    }

    private void handleEditEvent() {

    }

    private void openEventPopup(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/components/timetable/event-popup.fxml"));
            VBox content = loader.load();

            EventPopupController popupController = loader.getController();
            popupController.initialise(event);

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
