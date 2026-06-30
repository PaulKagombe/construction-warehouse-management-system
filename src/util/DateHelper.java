package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateHelper {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private DateHelper() {
    }

    public static String format(LocalDate date) {
        return date.format(DATE);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME);
    }
}