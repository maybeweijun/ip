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
     */
    public TaskList(List<Task> initial) {
        if (initial == null) {
            this.tasks = new ArrayList<>();
        } else {
            this.tasks = new ArrayList<>(initial);
        }
    }

    public int size() {
        return tasks.size();
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Convenient method to validate an index for the current list.
     */
    public boolean isValidIndex(int idx) {
        return idx >= 0 && idx < tasks.size();
    }

    /**
     * Returns the underlying list view, primarily for persistence operations.
     * Callers should avoid mutating the returned list directly outside this class.
     */
    public List<Task> toList() {
        return tasks;
    }
}
