package maybeweijun.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.Todo;

public class Storage {
    private final String filePath;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final int SPLIT_LIMIT_TWO = 2;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the configured file path in a lenient manner.
     * <p>
     * Behavior:
     * - Returns all tasks that can be parsed successfully.
     * - Skips malformed lines or unknown task types without failing the whole load.
     * - Swallows I/O exceptions and returns the tasks parsed up to the point of failure (if any).
     *
     * @return list of successfully parsed tasks from storage; never null
     */
    public List<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task parsed = parseTaskFromLine(line);
                if (parsed != null) {
                    tasks.add(parsed);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load state: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Serializes and writes the given tasks to the configured file path in a lenient manner.
     * <p>
     * Behavior:
     * - Writes each task on a new line in a compact pipe-separated format.
     * - Swallows I/O exceptions so that runtime save attempts do not crash the application.
     *
     * @param tasks list of tasks to persist
     */
    public void save(List<Task> tasks) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            for (Task task : tasks) {
                writer.write(buildSerialized(task));
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Failed to save state: " + e.getMessage());
        }
    }

    /**
     * Parses a single line from storage into a Task instance.
     * <p>
     * This method is lenient and returns null for malformed lines or unknown types.
     *
     * @param line raw line from the storage file
     * @return parsed Task, or null if the line cannot be parsed
     */
    private Task parseTaskFromLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        switch (type) {
            case "T":
                return parseTodo(description, isDone);
            case "D":
                if (parts.length >= 4) {
                    try {
                        String by = parts[3].trim();
                        return parseDeadline(description, by, isDone);
                    } catch (RuntimeException ex) {
                        // skip malformed date/time
                        return null;
                    }
                }
                return null;
            case "E":
                if (parts.length >= 4) {
                    String[] eventTimes = parts[3].split(" to ", SPLIT_LIMIT_TWO);
                    if (eventTimes.length == SPLIT_LIMIT_TWO) {
                        try {
                            String from = eventTimes[0].trim();
                            String to = eventTimes[1].trim();
                            return parseEvent(description, from, to, isDone);
                        } catch (RuntimeException ex) {
                            // skip malformed date/time
                            return null;
                        }
                    }
                }
                return null;
            default:
                // skip unknown types
                return null;
        }
    }

    /**
     * Creates a Todo task and applies done state.
     *
     * @param description description of the todo
     * @param isDone      whether the task is marked as done
     * @return the created Todo task
     */
    private Task parseTodo(String description, boolean isDone) {
        Task todo = new Todo(description);
        if (isDone) {
            todo.mark();
        }
        return todo;
        }

    /**
     * Creates a Deadline task from description and its due-by string, and applies done state.
     *
     * @param description description of the deadline
     * @param by          due-by string as stored
     * @param isDone      whether the task is marked as done
     * @return the created Deadline task
     * @throws RuntimeException if the underlying constructor fails to parse the by string
     */
    private Task parseDeadline(String description, String by, boolean isDone) {
        Task deadline = new Deadline(description, by);
        if (isDone) {
            deadline.mark();
        }
        return deadline;
    }

    /**
     * Creates an Event task from description and time range strings, and applies done state.
     *
     * @param description description of the event
     * @param from        start time string as stored
     * @param to          end time string as stored
     * @param isDone      whether the task is marked as done
     * @return the created Event task
     * @throws RuntimeException if the underlying constructor fails to parse the time strings
     */
    private Task parseEvent(String description, String from, String to, boolean isDone) {
        Task event = new Event(description, from, to);
        if (isDone) {
            event.mark();
        }
        return event;
    }

    /**
     * Builds a single-line, pipe-separated representation of a task suitable for storage.
     *
     * @param task the task to serialize
     * @return serialized line without a trailing line separator
     */
    private String buildSerialized(Task task) {
        StringBuilder sb = new StringBuilder();
        if (task instanceof Todo) {
            sb.append("T | ");
            sb.append(task.isDone() ? "1 | " : "0 | ");
            sb.append(task.getDescription());
        } else if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            sb.append("D | ");
            sb.append(task.isDone() ? "1 | " : "0 | ");
            sb.append(task.getDescription()).append(" | ")
              .append(d.getBy().format(FORMATTER));
        } else if (task instanceof Event) {
            Event e = (Event) task;
            sb.append("E | ");
            sb.append(task.isDone() ? "1 | " : "0 | ");
            sb.append(task.getDescription()).append(" | ")
              .append(e.getFrom().format(FORMATTER)).append(" to ")
              .append(e.getTo().format(FORMATTER));
        }
        return sb.toString();
    }
}
