package maybeweijun.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a time-bounded event with a start and end {@link LocalDateTime}.
 */
public class Event extends Task {

    protected LocalDateTime from;
    protected LocalDateTime to;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter PRINT_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy ha");

    /**
     * Creates an event using textual start and end datetimes in "yyyy-MM-dd HHmm" format.
     *
     * @param description description of the event
     * @param from        start datetime in the expected format
     * @param to          end datetime in the expected format
     * @throws RuntimeException if parsing fails
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDateTime.parse(from, FORMATTER);
        this.to = LocalDateTime.parse(to, FORMATTER);
    }

    /**
     * Creates an event using pre-parsed datetimes.
     *
     * @param description description of the event
     * @param from        start datetime
     * @param to          end datetime
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start datetime of the event.
     *
     * @return start as LocalDateTime
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end datetime of the event.
     *
     * @return end as LocalDateTime
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns the formatted display string including start and end times.
     *
     * @return display string
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
            + " (from: " + from.format(PRINT_FORMATTER)
            + " to: " + to.format(PRINT_FORMATTER) + ")";
    }
}