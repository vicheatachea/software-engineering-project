package dto;

import java.time.LocalDateTime;
import java.util.Locale;

public record AssignmentDTO(
		Long id, String type, LocalDateTime publishingDate, LocalDateTime deadline, String assignmentName,
		String subjectCode, String description, long timetableId, String localeCode) implements Event {
}
