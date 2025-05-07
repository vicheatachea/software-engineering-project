package dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a teaching session event.
 * Contains session scheduling information, location details, and descriptive content.
 * Implements the {@link Event} interface for integration with the timetable system.
 */
public record TeachingSessionDTO(
		Long id, LocalDateTime startDate, LocalDateTime endDate, String locationName, String subjectCode,
		String description, long timetableId, String localeCode) implements Event {
}
