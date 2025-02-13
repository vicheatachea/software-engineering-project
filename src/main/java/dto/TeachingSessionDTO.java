package dto;

import java.sql.Timestamp;

public record TeachingSessionDTO(Timestamp StartDate, Timestamp EndDate, String locationName,
                                 String subjectName) {
}
