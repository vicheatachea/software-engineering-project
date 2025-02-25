package dto;

import java.time.LocalDateTime;

public record TeachingSessionDTO(LocalDateTime startDate, LocalDateTime endDate, String locationName,
                                 String subjectName, String description) implements Event {
}
