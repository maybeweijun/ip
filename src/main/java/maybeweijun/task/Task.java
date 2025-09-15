package maybeweijun.task;

/**
 * Serves as the base class for all task types, encapsulating a description and completion status.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates a task with the specified description. Newly created tasks are not done.
     *
     * @param description textual description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return true if done; false otherwise
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the description of this task.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a simple string representation including completion state and description.
     *
     * @return formatted string, e.g. "[ ] read book"
     */
    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }
}
