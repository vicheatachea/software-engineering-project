package dto;

import java.time.LocalDateTime;

public record TeachingSessionDTO(Long id, LocalDateTime startDate, LocalDateTime endDate, String locationName,
                                 String subjectCode, String description, long timetableId) implements Event {
}
