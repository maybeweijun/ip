package maybeweijun.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import maybeweijun.exception.MaybeWeijunException;
import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.TaskList;
import maybeweijun.task.Todo;
import maybeweijun.ui.Ui;

public class Parser {

    public static boolean process(String input, TaskList tasks, Ui ui) throws MaybeWeijunException {
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
            throw new MaybeWeijunException.OnlyTodoException();
        } else if (input.equals("deadline")) {
            throw new MaybeWeijunException.OnlyDeadlineException();
        } else if (input.equals("event")) {
            throw new MaybeWeijunException.OnlyEventException();
        } else if (input.startsWith("todo")) {
            handleTodo(tasks, input, ui);
        } else if (input.startsWith("deadline")) {
            handleDeadline(tasks, input, ui);
        } else if (input.startsWith("event")) {
            handleEvent(tasks, input, ui);
        } else {
            throw new MaybeWeijunException.InvalidCommandException();
        }
        return false;
    }

    private static void printTaskList(TaskList tasks, Ui ui) {
        ui.printTaskList(tasks);
    }

    private static void handleMark(TaskList tasks, String input, Ui ui) throws MaybeWeijunException {
        try {
            int idx = Integer.parseInt(input.substring(5).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).mark();
                ui.printMarked(idx + 1, tasks.get(idx));
            } else {
                throw new MaybeWeijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new MaybeWeijunException.InvalidMarkException();
        }
    }

    private static void handleUnmark(TaskList tasks, String input, Ui ui) throws MaybeWeijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).unmark();
                ui.printUnmarked(idx + 1, tasks.get(idx));
            } else {
                throw new MaybeWeijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new MaybeWeijunException.InvalidUnmarkException();
        }
    }

    private static void handleDelete(TaskList tasks, String input, Ui ui) throws MaybeWeijunException {
        try {
            int idx = Integer.parseInt(input.substring(7).trim()) - 1;
            if (tasks.isValidIndex(idx)) {
                Task toRemove = tasks.get(idx);
                tasks.remove(idx);
                ui.printDeleted(toRemove, tasks.size());
            } else {
                throw new MaybeWeijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new MaybeWeijunException.InvalidDeleteException();
        }
    }

    private static void handleTodo(TaskList tasks, String input, Ui ui) throws MaybeWeijunException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new MaybeWeijunException.EmptyTodoException();
        }
        tasks.add(new Todo(description));
        printTaskAdded(tasks, ui);
    }

    private static void handleDeadline(TaskList tasks, String input, Ui ui) throws MaybeWeijunException {
        String[] parts = input.substring(9).split("/by", 2);
        if (parts.length == 2) {
            String description = parts[0].trim();
            String by = parts[1].trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new MaybeWeijunException.EmptyDeadlineException();
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
                LocalDateTime.parse(by, formatter);
            } catch (Exception e) {
                throw new MaybeWeijunException.InvalidDateTimeException();
            }
            tasks.add(new Deadline(description, by));
            printTaskAdded(tasks, ui);
        } else {
            throw new MaybeWeijunException.EmptyDeadlineException();
        }
    }

    private static void handleEvent(TaskList tasks, String input, Ui ui) throws MaybeWeijunException {
        String[] parts = input.substring(5).split("/from", 2);
        if (parts.length == 2) {
            String description = parts[0].trim();
            String[] timeParts = parts[1].split("/to", 2);
            if (timeParts.length == 2) {
                String start_datetime = timeParts[0].trim();
                String end_datetime = timeParts[1].trim();
                if (description.isEmpty() || start_datetime.isEmpty() || end_datetime.isEmpty()) {
                    throw new MaybeWeijunException.EmptyEventException();
                }
                LocalDateTime start;
                LocalDateTime end;
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
                    start = LocalDateTime.parse(start_datetime, formatter);
                    end = LocalDateTime.parse(end_datetime, formatter);
                } catch (Exception e) {
                    throw new MaybeWeijunException.InvalidDateTimeException();
                }
                if (!end.isAfter(start)) {
                    throw new MaybeWeijunException.InvalidDateRangeException();
                }
                tasks.add(new Event(description, start_datetime, end_datetime));
                printTaskAdded(tasks, ui);
            } else {
                throw new MaybeWeijunException.EmptyEventException();
            }
        } else {
            throw new MaybeWeijunException.EmptyEventException();
        }
    }

    private static void printTaskAdded(TaskList tasks, Ui ui) {
        ui.printTaskAdded(tasks.get(tasks.size() - 1));
    }
}
