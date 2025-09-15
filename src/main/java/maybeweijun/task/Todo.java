package maybeweijun.task;

/**
 * Represents a to-do task containing only a description and completion status, without date/time information.
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