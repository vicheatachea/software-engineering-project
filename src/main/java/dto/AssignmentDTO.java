package dto;

import java.time.LocalDateTime;

public record AssignmentDTO(Long id, String type, LocalDateTime publishingDate, LocalDateTime deadline,
                            String assignmentName, String subjectCode, String description, long timetableId,
                            String localeCode)
		implements Event {
}
