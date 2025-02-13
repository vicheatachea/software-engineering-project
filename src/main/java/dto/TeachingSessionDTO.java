package dto;

import entity.LocationEntity;
import entity.SubjectEntity;
import entity.TimetableEntity;

import java.sql.Timestamp;

public record TeachingSessionDTO(Timestamp StartDate, Timestamp EndDate, String locationName,
                                 String subjectName) {
}
