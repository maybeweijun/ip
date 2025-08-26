import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

public class maybeweijun {

    public static void main(String[] args) {
        printLogo();
        printQuery();
        handleUserInput();
    }

    private static void printLogo() {
        String logo = "[m-a-y-b-e-w-e-i-j-u-n]";
        System.out.println("Hello from\n" + logo + "\n");
    }

    private static void printQuery() {
        System.out.println("What can I do for you? \n");
    }

    private static void handleUserInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Task> tasks = loadState();
        while (true) {
            try {
                String input = reader.readLine();
                if (input == null) break;
                input = input.trim();

                try {
                    if (input.equalsIgnoreCase("bye")) {
                        exit();
                        break;
                    } else if (input.equalsIgnoreCase("list")) {
                        printTaskList(tasks);
                    } else if (input.startsWith("mark ")) {
                        handleMark(tasks, input);
                    } else if (input.startsWith("unmark ")) {
                        handleUnmark(tasks, input);
                    } else if (input.startsWith("delete ")) {
                        handleDelete(tasks, input);
                    } else if (input.equals("todo")) {
                        throw new maybeweijunException.OnlyTodoException();
                    } else if (input.equals("deadline")) {
                        throw new maybeweijunException.OnlyDeadlineException();
                    } else if (input.equals("event")) {
                        throw new maybeweijunException.OnlyEventException();
                    } else if (input.startsWith("todo")) {
                        handleTodo(tasks, input);
                    } else if (input.startsWith("deadline")) {
                        handleDeadline(tasks, input);
                    } else if (input.startsWith("event")) {
                        handleEvent(tasks, input);
                    } else {
                        throw new maybeweijunException.InvalidCommandException();
                    }
                    saveState(tasks);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
                break;
            }
        }
    }

    private static void printTaskList(ArrayList<Task> tasks) {
        System.out.println("-----------");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        System.out.println("-----------\n");
    }

    private static void handleMark(ArrayList<Task> tasks, String input) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(5).trim()) - 1;
            if (isValidIndex(tasks, idx)) {
                tasks.get(idx).mark();
                System.out.println("Marked task " + (idx + 1) + " as done.");
                System.out.println(tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidMarkException();
        }
    }

    private static void handleUnmark(ArrayList<Task> tasks, String input) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (isValidIndex(tasks, idx)) {
                tasks.get(idx).unmark();
                System.out.println("Unmarked task " + (idx + 1) + ".");
                System.out.println(tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidUnmarkException();
        }
    }

    private static void handleDelete(ArrayList<Task> tasks, String input) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (isValidIndex(tasks, idx)) {
                System.out.println("Noted. I've removed this task:");
                System.out.println(tasks.get(idx));
                tasks.remove(idx);
                System.out.println("Now you have " + tasks.size() + " tasks in the list.");
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidDeleteException();
        }
    }

    private static void handleTodo(ArrayList<Task> tasks, String input) throws maybeweijunException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new maybeweijunException.EmptyTodoException();
        }
        tasks.add(new Todo(description));
        printTaskAdded(tasks);
    }

    private static void handleDeadline(ArrayList<Task> tasks, String input) throws maybeweijunException {
        String[] parts = input.substring(9).split("/by", 2);
        if (parts.length == 2) {
            String description = parts[0].trim();
            String by = parts[1].trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new maybeweijunException.EmptyDeadlineException();
            }
            tasks.add(new Deadline(description, by));
            printTaskAdded(tasks);
        } else {
            throw new maybeweijunException.EmptyDeadlineException();
        }
    }

    private static void handleEvent(ArrayList<Task> tasks, String input) throws maybeweijunException {
        String[] parts = input.substring(5).split("/from", 2);
        if (parts.length == 2) {
            String description = parts[0].trim();
            String[] timeParts = parts[1].split("/to", 2);
            if (timeParts.length == 2) {
                String start_datetime = timeParts[0].trim();
                String end_datetime = timeParts[1].trim();
                if (description.isEmpty() || start_datetime.isEmpty() || end_datetime.isEmpty()) {
                    throw new maybeweijunException.EmptyEventException();
                }
                tasks.add(new Event(description, start_datetime, end_datetime));
                printTaskAdded(tasks);
            } else {
                throw new maybeweijunException.EmptyEventException();
            }
        } else {
            throw new maybeweijunException.EmptyEventException();
        }
    }

    private static void printTaskAdded(ArrayList<Task> tasks) {
        System.out.println("-----------\nadded: " + tasks.get(tasks.size() - 1) + "\n-----------\n");
    }

    private static boolean isValidIndex(ArrayList<Task> tasks, int idx) {
        return idx >= 0 && idx < tasks.size();
    }

    private static void exit() {
        System.out.println("Bye. Hope to see you again soon!\n");
    }

    public static void saveState(ArrayList<Task> tasks) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try (FileWriter writer = new FileWriter("state.txt", false)) {
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
                      .append(d.getBy().format(formatter));
                } else if (task instanceof Event) {
                    Event e = (Event) task;
                    sb.append("E | ");
                    sb.append(task.isDone() ? "1 | " : "0 | ");
                    sb.append(task.getDescription()).append(" | ")
                      .append(e.getFrom().format(formatter)).append(" to ")
                      .append(e.getTo().format(formatter));
                }
                writer.write(sb.toString());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Failed to save state: " + e.getMessage());
        }
    }

    public static ArrayList<Task> loadState() {
        ArrayList<Task> tasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try (BufferedReader reader = new BufferedReader(new FileReader("state.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;
                String type = parts[0].trim();
                boolean isDone = parts[1].trim().equals("1");
                String description = parts[2].trim();

                switch (type) {
                    case "T":
                        Task todo = new Todo(description);
                        if (isDone) todo.mark();
                        tasks.add(todo);
                        break;
                    case "D":
                        if (parts.length >= 4) {
                            String by = parts[3].trim();
                            Task deadline = new Deadline(description, by);
                            if (isDone) deadline.mark();
                            tasks.add(deadline);
                        }
                        break;
                    case "E":
                        if (parts.length >= 4) {
                            String[] eventTimes = parts[3].split(" to ", 2);
                            if (eventTimes.length == 2) {
                                String from = eventTimes[0].trim();
                                String to = eventTimes[1].trim();
                                Task event = new Event(description, from, to);
                                if (isDone) event.mark();
                                tasks.add(event);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load state: " + e.getMessage());
        }
        return tasks;
    }
}


