package view.controllers.components;

import javafx.fxml.FXML;

public class EventPopupController {
    private String eventType;

    public void initialise(String eventType) {
        this.eventType = eventType;
    }

    @FXML
    private void handleSaveData() {
        // Return data back to the timetable controller and close popup
    }
}
