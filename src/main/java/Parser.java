import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Parser {

    public static boolean process(String input, TaskList tasks) throws maybeweijunException {
        if (input == null) {
            return false;
        }
        input = input.trim();

        if (input.equalsIgnoreCase("bye")) {
            return true;
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
        return false;
    }

    private static void printTaskList(TaskList tasks) {
        Ui.printTaskList(tasks);
    }

    private static void handleMark(TaskList tasks, String input) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(5).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).mark();
                Ui.printMarked(idx + 1, tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidMarkException();
        }
    }

    private static void handleUnmark(TaskList tasks, String input) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).unmark();
                Ui.printUnmarked(idx + 1, tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidUnmarkException();
        }
    }

    private static void handleDelete(TaskList tasks, String input) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                Task toRemove = tasks.get(idx);
                tasks.remove(idx);
                Ui.printDeleted(toRemove, tasks.size());
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidDeleteException();
        }
    }

    private static void handleTodo(TaskList tasks, String input) throws maybeweijunException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new maybeweijunException.EmptyTodoException();
        }
        tasks.add(new Todo(description));
        printTaskAdded(tasks);
    }

    private static void handleDeadline(TaskList tasks, String input) throws maybeweijunException {
        String[] parts = input.substring(9).split("/by", 2);
        if (parts.length == 2) {
            String description = parts[0].trim();
            String by = parts[1].trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new maybeweijunException.EmptyDeadlineException();
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
                LocalDateTime.parse(by, formatter);
            } catch (Exception e) {
                throw new maybeweijunException.InvalidDateTimeException();
            }
            tasks.add(new Deadline(description, by));
            printTaskAdded(tasks);
        } else {
            throw new maybeweijunException.EmptyDeadlineException();
        }
    }

    private static void handleEvent(TaskList tasks, String input) throws maybeweijunException {
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
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
                    LocalDateTime.parse(start_datetime, formatter);
                    LocalDateTime.parse(end_datetime, formatter);
                } catch (Exception e) {
                    throw new maybeweijunException.InvalidDateTimeException();
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

    private static void printTaskAdded(TaskList tasks) {
        Ui.printTaskAdded(tasks.get(tasks.size() - 1));
    }
}
