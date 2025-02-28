package controller;

import dto.Event;
import model.EventModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventController {
	private final EventModel eventModel = new EventModel();

	// Fetch all events for a user within a time interval
	public List<Event> fetchEventsByUser(LocalDateTime startDate, LocalDateTime endDate, Long timetableId) {
		return eventModel.fetchEventsByUser(startDate, endDate, timetableId);
	}

	// Add an event for a user
	public void addEventForUser(Event event) {
		eventModel.addEventForUser(event);
	}

	// Update an event for a user
	public void updateEventForUser(Event event) {
		eventModel.updateEventForUser(event);
	}

	// Delete an event for a user
	public void deleteEventForUser(Event event) {
		eventModel.deleteEventForUser(event);
	}

	// Add an event for a group
	public void addEventForGroup(Event event, String groupName) {
		eventModel.addEventForGroup(event, groupName);
	}

	// Update an event for a group
	public void updateEventForGroup(Event event, String groupName) {
		eventModel.updateEventForGroup(event, groupName);
	}

	// Delete an event for a group
	public void deleteEventForGroup(Event event, String groupName) {
		eventModel.deleteEventForGroup(event, groupName);
	}
}
