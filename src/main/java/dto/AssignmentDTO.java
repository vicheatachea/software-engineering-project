package dto;

import java.sql.Timestamp;

public record AssignmentDTO(String type, Timestamp publishingDate, Timestamp deadline, String subjectName) {
}
