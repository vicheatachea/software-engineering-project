package controller.notifications;

import controller.EventController;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Service responsible for managing and sending notifications for upcoming events.
 * Periodically checks for events within the next hour and sends notifications
 * at configured time thresholds before each event.
 */
public class NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
	private static final int NOTIFICATION_CHECK = 60; // 1 minute

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private final List<NotificationStatus> notificationStatuses = new ArrayList<>();
	private final List<Integer> notificationTimes = List.of(5, 10, 30, 60);

	private final NotificationAware notificationAware;
	private final EventController eventController;

	/**
	 * Creates a new notification service that sends notifications to the specified recipient.
	 *
	 * @param notificationAware the component that will receive notifications
	 * @param eventController   the controller used to fetch upcoming events
	 */
	public NotificationService(NotificationAware notificationAware, EventController eventController) {
		this.notificationAware = notificationAware;
		this.eventController = eventController;
		startExecutor();
	}

	/**
	 * Starts the scheduled task that checks for events requiring notifications.
	 */
	private void startExecutor() {
		executorService.scheduleAtFixedRate(
				this::checkNotifications,
				0,
				NOTIFICATION_CHECK,
				java.util.concurrent.TimeUnit.SECONDS
		);
	}

	/**
	 * Checks for upcoming events and sends notifications if appropriate.
	 * Notifications are sent at 60, 30, 10, and 5 minutes before an event.
	 * Each notification threshold is only triggered once per event.
	 */
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
				                                                NotificationStatus newStatus =
						                                                new NotificationStatus(event);
				                                                notificationStatuses.add(newStatus);
				                                                return newStatus;
			                                                });

			LocalDateTime eventTime;
			switch (event) {
				case AssignmentDTO assignmentDTO -> eventTime = assignmentDTO.deadline();
				case TeachingSessionDTO teachingSessionDTO -> eventTime = teachingSessionDTO.startDate();
				default -> {
					logger.info("Unknown event type: {}", event.getClass().getName());
					continue;
				}
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

	/**
	 * Removes events that have already occurred from the notification tracking list.
	 *
	 * @param now the current time
	 */
	private void clearPastEvents(LocalDateTime now) {
		notificationStatuses.removeIf(status -> {
			LocalDateTime eventTime;
			if (status.getEvent() instanceof AssignmentDTO assignmentDTO) {
				eventTime = assignmentDTO.deadline();
			} else if (status.getEvent() instanceof TeachingSessionDTO teachingSessionDTO) {
				eventTime = teachingSessionDTO.startDate();
			} else {
				logger.info("Unknown event type: {}", status.getEvent().getClass().getName());
				return true; // Remove unknown event types
			}
			return eventTime.isBefore(now);
		});
	}

	/**
	 * Clears all tracked notification statuses.
	 */
	public synchronized void reset() {
		notificationStatuses.clear();
	}

	/**
	 * Shuts down the notification service and its scheduled executor.
	 */
	public void shutdown() {
		executorService.shutdown();
	}
}
