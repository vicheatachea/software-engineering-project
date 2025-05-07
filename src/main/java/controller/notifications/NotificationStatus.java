package controller.notifications;

import dto.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks notification status for a specific event.
 * Maintains information about which notifications have been sent for different time thresholds.
 */
public class NotificationStatus {
	private final Event event;
	private final Map<Integer, Boolean> statusMap = new HashMap<>();

	/**
	 * Creates a new notification status tracker for the specified event.
	 *
	 * @param event the event for which to track notification status
	 */
	public NotificationStatus(Event event) {
		this.event = event;
	}

	/**
	 * Returns the event associated with this notification status.
	 *
	 * @return the associated event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * Checks if a notification has been sent for the specified time threshold.
	 *
	 * @param minutes the time threshold in minutes
	 * @return true if a notification has been sent, false otherwise
	 */
	public boolean isNotified(int minutes) {
		return statusMap.getOrDefault(minutes, false);
	}

	/**
	 * Marks that a notification has been sent for the specified time threshold.
	 *
	 * @param minutes the time threshold in minutes
	 */
	public void setNotified(int minutes) {
		statusMap.put(minutes, true);
	}
}
