package view.controllers.components;

import dto.Event;
import javafx.fxml.FXML;

public class EventPopupController {
    private Event event;

    public void initialise(Event event) {
        this.event = event;
    }

    @FXML
    private void handleSaveData() {
        // Return data back to the timetable controller and close popup
    }
}
