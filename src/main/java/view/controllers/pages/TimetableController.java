package view.controllers.pages;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class TimetableController {
    @FXML
    private GridPane timetableGrid;

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

    private void addEvent() {
        // Get the start and end times, type, etc
        // Based on the start and end times allocate the necessary amount of cells
        // Change margins so that the times match the hours in relation to cell size
        // Maybe make a separate label component for this
    }
}
