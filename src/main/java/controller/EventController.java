package controller;

import dto.Event;
import model.EventModel;

import java.time.LocalDateTime;
import java.util.List;

public class EventController {
	private final EventModel eventModel = new EventModel();

	// Fetch all events for a user within a time interval
	public List<Event> fetchEventsByUser(LocalDateTime startDate, LocalDateTime endDate, long userId) {
		return eventModel.fetchEventsByUser(startDate, endDate, userId);
	}

	// Add an event for a user
	public void addEvent(Event event) {
		eventModel.addEvent(event);
	}

	// Update an event for a user
	public void updateEvent(Event event) {
		eventModel.updateEvent(event);
	}

	// Delete an event for a user
	public void deleteEvent(Event event) {
		eventModel.deleteEvent(event);
	}
}
