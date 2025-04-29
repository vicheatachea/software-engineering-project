package controller.notifications;

import controller.EventController;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NotificationService {
    private static final int NOTIFICATION_CHECK = 60; // 1 minute

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final List<NotificationStatus> notificationStatuses = new ArrayList<>();
    private final List<Integer> notificationTimes = List.of(5, 10, 30, 60);

    private final NotificationAware notificationAware;
    private final EventController eventController;

    public NotificationService(NotificationAware notificationAware, EventController eventController) {
        this.notificationAware = notificationAware;
        this.eventController = eventController;
        startExecutor();
    }

    private void startExecutor() {
        executorService.scheduleAtFixedRate(
                this::checkNotifications,
                0,
                NOTIFICATION_CHECK,
                java.util.concurrent.TimeUnit.SECONDS
        );
    }

    private synchronized void checkNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);
        List<Event> events = eventController.fetchEventsByUser(now, oneHourLater);

        clearPastEvents(now);

        for (Event event : events) {
            NotificationStatus status = notificationStatuses.stream()
                    .filter(ns -> ns.getEvent().equals(event))
                    .findFirst()
                    .orElseGet(() -> {
                        NotificationStatus newStatus = new NotificationStatus(event);
                        notificationStatuses.add(newStatus);
                        return newStatus;
                    });

            LocalDateTime eventTime;
            if (event instanceof AssignmentDTO) {
                eventTime = ((AssignmentDTO) event).deadline();
            } else if (event instanceof TeachingSessionDTO) {
                eventTime = ((TeachingSessionDTO) event).startDate();
            } else {
                System.out.println("Unknown event type: " + event.getClass().getName());
                continue;
            }

            int minutesUntilEvent = (int) ChronoUnit.MINUTES.between(now, eventTime);

            boolean notified = false;
            for (int time : notificationTimes) {
                if (minutesUntilEvent <= time && !status.isNotified(time)) {
                    if (!notified) {
                        notificationAware.notify(event, minutesUntilEvent);
                        notified = true;
                    }
                    status.setNotified(time);
                }
            }
        }
    }

    private void clearPastEvents(LocalDateTime now) {
        notificationStatuses.removeIf(status -> {
            LocalDateTime eventTime;
            if (status.getEvent() instanceof AssignmentDTO) {
                eventTime = ((AssignmentDTO) status.getEvent()).deadline();
            } else if (status.getEvent() instanceof TeachingSessionDTO) {
                eventTime = ((TeachingSessionDTO) status.getEvent()).startDate();
            } else {
                System.out.println("Unknown event type: " + status.getEvent().getClass().getName());
                return true; // Remove unknown event types
            }
            return eventTime.isBefore(now);
        });
    }

    public synchronized void reset() {
        notificationStatuses.clear();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
