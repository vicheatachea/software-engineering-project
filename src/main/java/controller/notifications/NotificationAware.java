package controller.notifications;

import dto.Event;

/**
 * Interface for components that can receive notifications about events.
 * Implementing classes will be notified about upcoming events with timing information.
 */
public interface NotificationAware {
	/**
	 * Notifies the implementing class about an upcoming event.
	 *
	 * @param event the event for which the notification is being sent
	 * @param time  the time in minutes until the event occurs
	 */
	void notify(Event event, int time);
}
