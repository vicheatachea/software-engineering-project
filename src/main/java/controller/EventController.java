package controller;

import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import model.EventModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventController {
    private final EventModel eventModel = new EventModel();

    // Fetch all events for a user within a time interval
    public List<Event> fetchEventsByUser(LocalDate startDate, LocalDate endDate) {
        // Placeholder
        LocalDateTime testStart = LocalDateTime.of(2025, 3, 1, 0, 0);
        LocalDateTime testEnd = LocalDateTime.of(2025, 3, 1, 12, 0);
        TeachingSessionDTO testTeachingSession = new TeachingSessionDTO(1L, testStart, testEnd, "Somewhere", "Test Subject", "Test Description");
        LocalDateTime testStart2 = LocalDateTime.of(2025, 2, 28, 9, 0);
        LocalDateTime testEnd2 = LocalDateTime.of(2025, 3, 1, 10, 45);
        AssignmentDTO testAssignment = new AssignmentDTO(2L, "Test Type", testStart2, testEnd2, "Test Assignment", "Test Subject", "Test Description");
        return new ArrayList<>(List.of(testTeachingSession, testAssignment));
    }

    // Add an event for a user
    public void addEventForUser(Event event) {
        // Placeholder
    }

    // Update an event for a user
    public void updateEventForUser(Event event) {
        // Placeholder
    }

    // Delete an event for a user
    public void deleteEventForUser(Event event) {
        // Placeholder
    }

    // NOTE: groupName should be unique

    // Add an event for a group
    public void addEventForGroup(Event event, String groupName) {
        // Placeholder
    }

    // Update an event for a group
    public void updateEventForGroup(Event event, String groupName) {
        // Placeholder
    }

    // Delete an event for a group
    public void deleteEventForGroup(Event event, String groupName) {
        // Placeholder
    }
}
