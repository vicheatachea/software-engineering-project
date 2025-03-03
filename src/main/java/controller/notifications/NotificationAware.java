package controller.notifications;

import dto.Event;

public interface NotificationAware {
    void notify(Event event, int time);
}
