package maybeweijun.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import maybeweijun.exception.maybeweijunException;
import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.TaskList;
import maybeweijun.task.Todo;
import maybeweijun.ui.Ui;

public class Parser {

    public static boolean process(String input, TaskList tasks, Ui ui) throws maybeweijunException {
        if (input == null) {
            return false;
        }
        input = input.trim();

        if (input.equalsIgnoreCase("bye")) {
            return true;
        } else if (input.equalsIgnoreCase("list")) {
            printTaskList(tasks, ui);
        } else if (input.startsWith("mark ")) {
            handleMark(tasks, input, ui);
        } else if (input.startsWith("unmark ")) {
            handleUnmark(tasks, input, ui);
        } else if (input.startsWith("delete ")) {
            handleDelete(tasks, input, ui);
        } else if (input.equals("todo")) {
            throw new maybeweijunException.OnlyTodoException();
        } else if (input.equals("deadline")) {
            throw new maybeweijunException.OnlyDeadlineException();
        } else if (input.equals("event")) {
            throw new maybeweijunException.OnlyEventException();
        } else if (input.startsWith("todo")) {
            handleTodo(tasks, input, ui);
        } else if (input.startsWith("deadline")) {
            handleDeadline(tasks, input, ui);
        } else if (input.startsWith("event")) {
            handleEvent(tasks, input, ui);
        } else if (input.startsWith("find ")) {
            handleFind(tasks, input, ui);
        } else {
            throw new maybeweijunException.InvalidCommandException();
        }
        return false;
    }

    private static void printTaskList(TaskList tasks, Ui ui) {
        ui.printTaskList(tasks);
    }

    private static void handleMark(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(5).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).mark();
                ui.printMarked(idx + 1, tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidMarkException();
        }
    }

    private static void handleUnmark(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).unmark();
                ui.printUnmarked(idx + 1, tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidUnmarkException();
        }
    }

    private static void handleDelete(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                Task toRemove = tasks.get(idx);
                tasks.remove(idx);
                ui.printDeleted(toRemove, tasks.size());
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidDeleteException();
        }
    }

    /**
     * Gives user a way to find task by searchng for keyword in the task description.
     * Lists out the tasks given the
     * @param tasks
     * @param input
     * @param ui
     * @throws maybeweijunException
     */
    private static void handleFind(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        String description = input.substring(5).trim(); // Retrieve description
        if (description.isEmpty()) {
            throw new maybeweijunException.EmptyFindException();
        }
        TaskList foundTasks = new TaskList();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(description.toLowerCase())) {
                foundTasks.add(task);
            }
        }
        ui.printTaskList(foundTasks);
    }
    private static void handleTodo(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new maybeweijunException.EmptyTodoException();
        }
        tasks.add(new Todo(description));
        printTaskAdded(tasks, ui);
    }

    private static void handleDeadline(TaskList tasks, String input, Ui ui) throws maybeweijunException {
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
            printTaskAdded(tasks, ui);
        } else {
            throw new maybeweijunException.EmptyDeadlineException();
        }
    }

    private static void handleEvent(TaskList tasks, String input, Ui ui) throws maybeweijunException {
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
                LocalDateTime start;
                LocalDateTime end;
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
                    start = LocalDateTime.parse(start_datetime, formatter);
                    end = LocalDateTime.parse(end_datetime, formatter);
                } catch (Exception e) {
                    throw new maybeweijunException.InvalidDateTimeException();
                }
                if (!end.isAfter(start)) {
                    throw new maybeweijunException.InvalidDateRangeException();
                }
                tasks.add(new Event(description, start_datetime, end_datetime));
                printTaskAdded(tasks, ui);
            } else {
                throw new maybeweijunException.EmptyEventException();
            }
        } else {
            throw new maybeweijunException.EmptyEventException();
        }
    }

    private static void printTaskAdded(TaskList tasks, Ui ui) {
        ui.printTaskAdded(tasks.get(tasks.size() - 1));
    }
}
