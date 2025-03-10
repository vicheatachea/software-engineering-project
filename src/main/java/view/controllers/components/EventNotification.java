package view.controllers.components;

import dto.Event;

public class EventNotification {
    private final Event event;
    private final int time;

    public EventNotification(Event event, int time) {
        this.event = event;
        this.time = time;
    }

    public Event getEvent() {
        return event;
    }

    public int getTime() {
        return time;
    }
}
