package maybeweijun.task;

/**
 * Represents a simple "to-do" task that contains only a textual description
 * and a completion status inherited from {@link Task}.
 * A {@code Todo} does not carry any date/time information.
 */
public class Todo extends Task {

    /**
     * Creates a {@code Todo} with the given description.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of this todo, prefixed with {@code [T]},
     * followed by the generic {@link Task} representation which includes completion
     * status and description.
     *
     * @return formatted string for display, e.g. {@code "[T][ ] buy milk"}
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

}