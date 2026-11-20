package chocan.model;

import chocan.util.Formats;
import java.time.LocalDate;

/**
 * An inclusive range of dates that a report or accounting run covers. Service
 * records are included when the day they were received falls inside the range.
 *
 * @author [TEAM MEMBER NAME]
 */
public class ReportPeriod {

    private final LocalDate from;
    private final LocalDate to;

    public ReportPeriod(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both a start and an end date are required.");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("The end date cannot be before the start date.");
        }
        this.from = from;
        this.to = to;
    }

    /** The default weekly period: the six days before {@code today} plus today. */
    public static ReportPeriod lastSevenDays(LocalDate today) {
        return new ReportPeriod(today.minusDays(6), today);
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public String toString() {
        return Formats.date(from) + " through " + Formats.date(to);
    }
}
