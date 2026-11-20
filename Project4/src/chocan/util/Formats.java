package chocan.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Shared formatting/parsing helpers so dates and money look the same in every
 * menu and report (dates use the MM-DD-YYYY format from the requirements).
 *
 * @author [TEAM MEMBER NAME]
 */
public final class Formats {

    public static final String DATE_HINT = "MM-DD-YYYY";

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("MM-dd-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("MM-dd-uuuu HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    private Formats() {
        // utility class
    }

    public static String date(LocalDate date) {
        return DATE.format(date);
    }

    public static String dateTime(LocalDateTime dateTime) {
        return DATE_TIME.format(dateTime);
    }

    /** Parses MM-DD-YYYY, throwing an {@link IllegalArgumentException} with a friendly message. */
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim(), DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Please enter a valid date as " + DATE_HINT + ".");
        }
    }

    public static String money(BigDecimal amount) {
        return String.format("$%,.2f", amount);
    }

    /** A line of {@code count} copies of {@code ch}, used for report borders. */
    public static String line(char ch, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
