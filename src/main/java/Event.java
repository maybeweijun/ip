import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter PRINT_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy ha");

    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDateTime.parse(from, FORMATTER);
        this.to = LocalDateTime.parse(to, FORMATTER);
    }

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
            + " (from: " + from.format(PRINT_FORMATTER)
            + " to: " + to.format(PRINT_FORMATTER) + ")";
    }
}