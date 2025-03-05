package view.controllers.components;

import dto.Event;
import javafx.scene.control.Label;

public class EventLabel extends Label {
    private Event event;

    public EventLabel(Event event) {
        // Placeholder
        super("Event");
        this.event = event;
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public Event getEvent() {
        return event;
    }
}
