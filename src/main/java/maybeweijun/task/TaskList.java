package maybeweijun.task;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskList is a simple OOP wrapper around an ArrayList of Task objects.
 * It exposes only the operations needed by the application code, helping to
 * encapsulate list operations and make future extensions easier.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList initialized with the given tasks.
     * The provided list is copied into an internal ArrayList.
     *
     * @param initial the initial tasks to populate, may be null
     */
    public TaskList(List<Task> initial) {
        if (initial == null) {
            this.tasks = new ArrayList<>();
        } else {
            this.tasks = new ArrayList<>(initial);
        }
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current size
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index zero-based index
     * @return the task at index
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Index out of bounds: " + index;
        return tasks.get(index);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index zero-based index
     * @return the removed task
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Convenient method to validate an index for the current list.
     *
     * @param idx zero-based index to validate
     * @return true if valid; false otherwise
     */
    public boolean isValidIndex(int idx) {
        return idx >= 0 && idx < tasks.size();
    }

    /**
     * Returns the underlying list view, primarily for persistence operations.
     * Callers should avoid mutating the returned list directly outside this class.
     *
     * @return the backing list of tasks
     */
    public List<Task> toList() {
        return tasks;
    }
}
