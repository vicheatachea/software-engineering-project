package controller;

import dto.Event;
import model.EventModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class for managing events in the system.
 * Handles operations for retrieving, creating, updating, and deleting events
 * by interfacing with the EventModel layer.
 */
public class EventController {
	private final EventModel eventModel = new EventModel();

	/**
	 * Retrieves all events for the current user within a specified time period.
	 *
	 * @param startDate The beginning of the time interval to fetch events from
	 * @param endDate   The end of the time interval to fetch events from
	 * @return A list of events for the user in the specified time frame
	 */
	public List<Event> fetchEventsByUser(LocalDateTime startDate, LocalDateTime endDate) {
		return eventModel.fetchEventsByUser(startDate, endDate);
	}

	/**
	 * Retrieves events for the current user within a time period that match a specific locale.
	 *
	 * @param startDate  The beginning of the time interval to fetch events from
	 * @param endDate    The end of the time interval to fetch events from
	 * @param localeCode The locale code to filter events by
	 * @return A list of events matching the criteria
	 */
	public List<Event> fetchEventsByUserAndLocale(LocalDateTime startDate, LocalDateTime endDate, String localeCode) {
		return eventModel.fetchEventsByUserAndLocale(startDate, endDate, localeCode);
	}

	/**
	 * Creates a new event for the current user.
	 *
	 * @param event The event data to add
	 */
	public void addEvent(Event event) {
		eventModel.addEvent(event);
	}

	/**
	 * Updates an existing event's information.
	 *
	 * @param event The updated event data
	 */
	public void updateEvent(Event event) {
		eventModel.updateEvent(event);
	}

	/**
	 * Removes an event from the system.
	 *
	 * @param event The event to delete
	 */
	public void deleteEvent(Event event) {
		eventModel.deleteEvent(event);
	}

	/**
	 * Deletes all events from the system.
	 * This is a maintenance operation and should be used with caution.
	 */
	public void deleteAllEvents() {
		eventModel.deleteAllEvents();
	}
}
