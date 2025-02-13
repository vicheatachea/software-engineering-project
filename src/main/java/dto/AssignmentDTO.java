package dto;

import entity.SubjectEntity;
import entity.TimetableEntity;

import java.sql.Timestamp;

public record AssignmentDTO(String type, Timestamp publishingDate, Timestamp deadline, String subjectName) {
}
