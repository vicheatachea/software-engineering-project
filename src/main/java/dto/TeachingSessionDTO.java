package dto;

import java.time.LocalDateTime;
import java.util.Locale;

public record TeachingSessionDTO(
        Long id, LocalDateTime startDate, LocalDateTime endDate, String locationName, String subjectCode,
        String description, long timetableId, String localeCode) implements Event {
}
