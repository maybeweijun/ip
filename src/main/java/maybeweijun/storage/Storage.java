package maybeweijun.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import maybeweijun.exception.maybeweijunException;
import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.Todo;

/**
 * Storage provides persistence for tasks using a simple, line-based pipe-delimited format.
 * It offers lenient operations (load/save) that tolerate malformed lines or IO issues.
 */
public class Storage {
    private final String filePath;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     *  Given string of path, creates a new Storage object.
     * @param filePath path to the file to be used.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads file from filePath, and for each line, create the corresponding
     * task object and add it to the list.
     * @return List of tasks read from the file.
     */
    public List<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;
                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("1");
                String description = parts[2].trim();

                switch (type) {
                    case "T": {
                        Task todo = new Todo(description);
                        if (isDone) todo.mark();
                        tasks.add(todo);
                        break;
                    }
                    case "D": {
                        if (parts.length >= 4) {
                            try {
                                String by = parts[3].trim();
                                Task deadline = new Deadline(description, by);
                                if (isDone) deadline.mark();
                                tasks.add(deadline);
                            } catch (RuntimeException ex) {
                                // skip malformed date/time
                            }
                        }
                        break;
                    }
                    case "E": {
                        if (parts.length >= 4) {
                            String[] eventTimes = parts[3].split(" to ", 2);
                            if (eventTimes.length == 2) {
                                try {
                                    String from = eventTimes[0].trim();
                                    String to = eventTimes[1].trim();
                                    Task event = new Event(description, from, to);
                                    if (isDone) event.mark();
                                    tasks.add(event);
                                } catch (RuntimeException ex) {
                                    // skip malformed date/time
                                }
                            }
                        }
                        break;
                    }
                    default:
                        // skip unknown types
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load state: " + e.getMessage());
        }
        return tasks;
    }



    /**
     * Lenient save: swallows IO issues, used by app runtime.
     */
    public void save(List<Task> tasks) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            for (Task task : tasks) {
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
                writer.write(sb.toString());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Failed to save state: " + e.getMessage());
        }
    }

}
