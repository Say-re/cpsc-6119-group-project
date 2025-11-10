package util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting dates consistently across the dashboard
 */
public class DateFormatter {
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final DateTimeFormatter TIME_FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Format Instant to date string (yyyy-MM-dd)
     */
    public static String format(Instant instant) {
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return ldt.format(DATE_FORMATTER);
    }

    /**
     * Format Instant to datetime string (yyyy-MM-dd HH:mm)
     */
    public static String formatDateTime(Instant instant) {
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return ldt.format(DATETIME_FORMATTER);
    }

    /**
     * Format Instant to time string (HH:mm:ss)
     */
    public static String formatTime(Instant instant) {
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return ldt.format(TIME_FORMATTER);
    }
}
