package maybeweijun.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that has a deadline at a specific date and time.
 * The internal timestamp is stored as a {@link LocalDateTime}.
 */
public class Deadline extends Task {

    protected LocalDateTime by;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter PRINT_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy ha");

    /**
     * Creates a deadline with the given description and deadline date/time.
     * @param description description of the task
     * @param by deadline date/time, in the format "yyyy-MM-dd HHmm"
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, FORMATTER);
    }


    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(PRINT_FORMATTER) + ")";
    }
}
