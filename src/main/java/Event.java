public class Event extends Task {
    protected String start_datetime;
    protected String end_datetime;
    public Event(String description,String start_datetime, String end_datetime) {
        super(description);
        this.start_datetime = start_datetime;
        this.end_datetime = end_datetime;
    }
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start_datetime + " to: " + end_datetime + ")";
    }
}