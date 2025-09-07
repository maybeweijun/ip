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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    // Command strings to avoid magic numbers for substring indices
    private static final String CMD_BYE = "bye";
    private static final String CMD_LIST = "list";
    private static final String CMD_MARK = "mark ";
    private static final String CMD_UNMARK = "unmark ";
    private static final String CMD_DELETE = "delete ";
    private static final String CMD_TODO = "todo";
    private static final String CMD_DEADLINE = "deadline";
    private static final String CMD_EVENT = "event";
    private static final String CMD_FIND = "find ";

    // Shared numeric constants
    private static final int SPLIT_LIMIT_TWO = 2;
    private static final int ONE_BASED_OFFSET = 1;
    private static final int LAST_INDEX_OFFSET = 1;

    public static boolean process(String input, TaskList tasks, Ui ui) throws maybeweijunException {
        if (input == null) {
            return false;
        }
        input = input.trim();

        if (input.equalsIgnoreCase(CMD_BYE)) {
            return true;
        } else if (input.equalsIgnoreCase(CMD_LIST)) {
            printTaskList(tasks, ui);
        } else if (input.startsWith(CMD_MARK)) {
            handleMark(tasks, input, ui);
        } else if (input.startsWith(CMD_UNMARK)) {
            handleUnmark(tasks, input, ui);
        } else if (input.startsWith(CMD_DELETE)) {
            handleDelete(tasks, input, ui);
        } else if (input.equals(CMD_TODO)) {
            throw new maybeweijunException.OnlyTodoException();
        } else if (input.equals(CMD_DEADLINE)) {
            throw new maybeweijunException.OnlyDeadlineException();
        } else if (input.equals(CMD_EVENT)) {
            throw new maybeweijunException.OnlyEventException();
        } else if (input.startsWith(CMD_TODO)) {
            handleTodo(tasks, input, ui);
        } else if (input.startsWith(CMD_DEADLINE)) {
            handleDeadline(tasks, input, ui);
        } else if (input.startsWith(CMD_EVENT)) {
            handleEvent(tasks, input, ui);
        } else if (input.startsWith(CMD_FIND)) {
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
            int idx = Integer.parseInt(input.substring(CMD_MARK.length()).trim()) - ONE_BASED_OFFSET;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).mark();
                ui.printMarked(idx + ONE_BASED_OFFSET, tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidMarkException();
        }
    }

    private static void handleUnmark(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(CMD_UNMARK.length()).trim()) - ONE_BASED_OFFSET;
            if (tasks.isValidIndex(idx)) {
                tasks.get(idx).unmark();
                ui.printUnmarked(idx + ONE_BASED_OFFSET, tasks.get(idx));
            } else {
                throw new maybeweijunException.InvalidTaskNumberException();
            }
        } catch (NumberFormatException e) {
            throw new maybeweijunException.InvalidUnmarkException();
        }
    }

    private static void handleDelete(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        try {
            int idx = Integer.parseInt(input.substring(CMD_DELETE.length()).trim()) - ONE_BASED_OFFSET;
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
     * Gives user a way to find task by searching for keyword in the task description.
     */
    private static void handleFind(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        String description = input.substring(CMD_FIND.length()).trim();
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
        String description = input.substring(CMD_TODO.length()).trim();
        if (description.isEmpty()) {
            throw new maybeweijunException.EmptyTodoException();
        }
        tasks.add(new Todo(description));
        printTaskAdded(tasks, ui);
    }

    private static void handleDeadline(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        String[] parts = input.substring(CMD_DEADLINE.length()).split("/by", SPLIT_LIMIT_TWO);
        if (parts.length == SPLIT_LIMIT_TWO) {
            String description = parts[0].trim();
            String by = parts[1].trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new maybeweijunException.EmptyDeadlineException();
            }
            try {
                LocalDateTime.parse(by, FORMATTER);
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
        String[] parts = input.substring(CMD_EVENT.length()).split("/from", SPLIT_LIMIT_TWO);
        if (parts.length == SPLIT_LIMIT_TWO) {
            String description = parts[0].trim();
            String[] timeParts = parts[1].split("/to", SPLIT_LIMIT_TWO);
            if (timeParts.length == SPLIT_LIMIT_TWO) {
                String start_datetime = timeParts[0].trim();
                String end_datetime = timeParts[1].trim();
                if (description.isEmpty() || start_datetime.isEmpty() || end_datetime.isEmpty()) {
                    throw new maybeweijunException.EmptyEventException();
                }
                LocalDateTime start;
                LocalDateTime end;
                try {
                    start = LocalDateTime.parse(start_datetime, FORMATTER);
                    end = LocalDateTime.parse(end_datetime, FORMATTER);
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
        ui.printTaskAdded(tasks.get(tasks.size() - LAST_INDEX_OFFSET));
    }
}
