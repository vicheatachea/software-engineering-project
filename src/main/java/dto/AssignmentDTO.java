package dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing an academic assignment.
 * Contains assignment identification, scheduling information, content details, and metadata.
 * Implements the {@link Event} interface for integration with the timetable system.
 */
public record AssignmentDTO(
		Long id, String type, LocalDateTime publishingDate, LocalDateTime deadline, String assignmentName,
		String subjectCode, String description, long timetableId, String localeCode) implements Event {
}
