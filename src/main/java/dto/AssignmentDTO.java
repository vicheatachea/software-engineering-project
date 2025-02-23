package dto;

import java.time.LocalDateTime;

public record AssignmentDTO(String type, LocalDateTime publishingDate, LocalDateTime deadline,
                            String assignmentName, String subjectName, String description) implements Event {
}
