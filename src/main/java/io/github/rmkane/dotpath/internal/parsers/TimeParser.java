package io.github.rmkane.dotpath.internal.parsers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for parsing date and time strings into their respective types.
 * Uses ISO standard formats for consistent parsing.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeParser {
    /**
     * Parses a string into a LocalDate using ISO_DATE format (yyyy-MM-dd).
     *
     * @param str The string to parse, must be in ISO date format
     * @return The parsed LocalDate
     * @throws java.time.format.DateTimeParseException if the string cannot be parsed
     */
    public static LocalDate parseLocalDate(String str) {
        return LocalDate.parse(str, DateTimeFormatter.ISO_DATE);
    }

    /**
     * Parses a string into a LocalDateTime using ISO_DATE_TIME format (yyyy-MM-ddTHH:mm:ss).
     *
     * @param str The string to parse, must be in ISO date-time format
     * @return The parsed LocalDateTime
     * @throws java.time.format.DateTimeParseException if the string cannot be parsed
     */
    public static LocalDateTime parseLocalDateTime(String str) {
        return LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME);
    }
}
