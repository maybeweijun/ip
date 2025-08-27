package maybeweijun.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import maybeweijun.exception.MaybeWeijunException;
import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.Todo;

public class Storage {
    private final String filePath;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public Storage(String filePath) {
        this.filePath = filePath;
        }

    /**
     * Lenient load: returns whatever can be parsed, skips malformed lines, and swallows IO issues.
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
     * Strict load: throws on IO or malformed content.
     */
    public List<Task> loadOrThrow() throws MaybeWeijunException {
        ArrayList<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                String[] parts = line.split("\\|");
                if (parts.length < 3) {
                    throw new MaybeWeijunException.InvalidStorageFormatException(
                            "Malformed line " + lineNo + ": " + line);
                }
                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("1");
                String description = parts[2].trim();

                try {
                    switch (type) {
                        case "T": {
                            Task todo = new Todo(description);
                            if (isDone) todo.mark();
                            tasks.add(todo);
                            break;
                        }
                        case "D": {
                            if (parts.length < 4) {
                                throw new MaybeWeijunException.InvalidStorageFormatException(
                                        "Missing deadline datetime at line " + lineNo + ": " + line);
                            }
                            String by = parts[3].trim();
                            Task deadline = new Deadline(description, by);
                            if (isDone) deadline.mark();
                            tasks.add(deadline);
                            break;
                        }
                        case "E": {
                            if (parts.length < 4) {
                                throw new MaybeWeijunException.InvalidStorageFormatException(
                                        "Missing event datetime(s) at line " + lineNo + ": " + line);
                            }
                            String[] eventTimes = parts[3].split(" to ", 2);
                            if (eventTimes.length != 2) {
                                throw new MaybeWeijunException.InvalidStorageFormatException(
                                        "Invalid event times at line " + lineNo + ": " + line);
                            }
                            String from = eventTimes[0].trim();
                            String to = eventTimes[1].trim();
                            Task event = new Event(description, from, to);
                            if (isDone) event.mark();
                            tasks.add(event);
                            break;
                        }
                        default:
                            throw new MaybeWeijunException.InvalidStorageFormatException(
                                    "Unknown task type at line " + lineNo + ": " + type);
                    }
                } catch (RuntimeException ex) {
                    throw new MaybeWeijunException.InvalidStorageFormatException(
                            "Invalid date/time or content at line " + lineNo + ": " + line);
                }
            }
        } catch (IOException e) {
            throw new MaybeWeijunException.StorageLoadException("Failed to load state: " + e.getMessage());
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

    /**
     * Strict save: throws on IO errors.
     */
    public void saveOrThrow(List<Task> tasks) throws MaybeWeijunException {
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
            throw new MaybeWeijunException.StorageSaveException("Failed to save state: " + e.getMessage());
        }
    }
}
