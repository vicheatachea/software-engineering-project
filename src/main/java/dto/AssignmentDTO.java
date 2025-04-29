package dto;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * AssignmentDTO is a Data Transfer Object (DTO) class that represents an assignment.
 * It is used to transfer data between different layers of the application.
 */
public record AssignmentDTO(
		Long id, String type, LocalDateTime publishingDate, LocalDateTime deadline, String assignmentName,
		String subjectCode, String description, long timetableId, String localeCode) implements Event {
}
