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

/**
 * Parser is responsible for interpreting user input commands and orchestrating
 * the corresponding operations on a {@link TaskList}, delegating user-facing
 * messages to the {@link Ui}. All methods are static and the class is stateless.
 */
public class Parser {

    /**
     * Processes a single user input line and executes the corresponding command.
     *
     * @param input the raw user input
     * @param tasks the mutable list of tasks to operate on
     * @param ui    the UI used to print user-facing messages
     * @return true if the command indicates the program should terminate; false otherwise
     * @throws maybeweijunException if the input is invalid or parameters are missing
     */
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
        } else {
            throw new maybeweijunException.InvalidCommandException();
        }
        return false;
    }

    /**
     * Prints the current list of tasks to the UI.
     *
     * @param tasks the list to print
     * @param ui    the UI for output
     */
    private static void printTaskList(TaskList tasks, Ui ui) {
        ui.printTaskList(tasks);
    }

    /**
     * Marks the specified task (1-based index) as done.
     *
     * @param tasks the task list
     * @param input the full command input containing the index
     * @param ui    the UI for feedback
     * @throws maybeweijunException if parsing fails or the index is invalid
     */
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

    /**
     * Unmarks the specified task (1-based index).
     *
     * @param tasks the task list
     * @param input the full command input containing the index
     * @param ui    the UI for feedback
     * @throws maybeweijunException if parsing fails or the index is invalid
     */
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

    /**
     * Deletes the specified task (1-based index) from the list.
     *
     * @param tasks the task list
     * @param input the full command input containing the index
     * @param ui    the UI for feedback
     * @throws maybeweijunException if parsing fails or the index is invalid
     */
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
     * Creates a Todo from the input and adds it to the list.
     *
     * @param tasks the task list
     * @param input the full command input containing description
     * @param ui    the UI for feedback
     * @throws maybeweijunException if the description is empty
     */
    private static void handleTodo(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new maybeweijunException.EmptyTodoException();
        }
        tasks.add(new Todo(description));
        printTaskAdded(tasks, ui);
    }

    /**
     * Creates a Deadline from the input and adds it to the list.
     *
     * @param tasks the task list
     * @param input the full command input containing description and /by datetime
     * @param ui    the UI for feedback
     * @throws maybeweijunException if fields are missing or the datetime is invalid
     */
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

    /**
     * Creates an Event from the input and adds it to the list.
     *
     * @param tasks the task list
     * @param input the full command input containing description, /from and /to datetimes
     * @param ui    the UI for feedback
     * @throws maybeweijunException if fields are missing, date-times are invalid, or the end is not after the start
     */
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

    /**
     * Notifies the UI that a task has been added, printing the last task in the list.
     *
     * @param tasks the task list
     * @param ui    the UI for feedback
     */
    private static void printTaskAdded(TaskList tasks, Ui ui) {
        ui.printTaskAdded(tasks.get(tasks.size() - 1));
    }
}
