package controller.notifications;

import dto.Event;
import java.util.HashMap;
import java.util.Map;

public class NotificationStatus {
    private final Event event;
    private final Map<Integer, Boolean> statusMap = new HashMap<>();

    public NotificationStatus(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isNotified(int minutes) {
        return statusMap.getOrDefault(minutes, false);
    }

    public void setNotified(int minutes) {
        statusMap.put(minutes, true);
    }
}
