package dto;

import java.time.LocalDateTime;

public record TeachingSessionDTO(Long id, LocalDateTime startDate, LocalDateTime endDate, String locationName,
                                 String subjectName, String description, Long timetableId) implements Event {
}
