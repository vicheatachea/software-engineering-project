package dto;

import java.time.LocalDateTime;

public record AssignmentDTO(Long id, String type, LocalDateTime publishingDate, LocalDateTime deadline,
                            String assignmentName, String subjectName, String description, long timetableId)
		implements Event {
}
