package util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatterUtil {
    private static final String TIME_FORMAT = "HH:mm";
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static LocalTime getTimeFromString(String time) {
        return LocalTime.parse(time, timeFormatter);
    }

    public static String getTimeFromDateTime(LocalDateTime dateTime) {
        return dateTime.format(timeFormatter);
    }
}
