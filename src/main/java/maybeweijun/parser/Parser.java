package maybeweijun.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import maybeweijun.exception.maybeweijunException;
import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.TaskList;
import maybeweijun.task.Todo;
import maybeweijun.ui.Ui;

/**
 * Parses and executes user commands to manipulate a {@link TaskList}.
 *
 * <p>Recognized commands include adding todos, deadlines and events, listing tasks,
 * marking/unmarking tasks, deleting tasks, and finding tasks by keyword. The parser
 * validates arguments, parses date/time values using the {@code yyyy-MM-dd HHmm}
 * pattern defined by {@code FORMATTER}, and delegates user-facing output to {@link Ui}.
 *
 * <p>Invalid input is signaled by throwing the appropriate subclass of
 * {@link maybeweijun.exception.maybeweijunException}.
 *
 * @see TaskList
 * @see Ui
 * @see maybeweijun.exception.maybeweijunException
 */
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
    private static final String CMD_SORT = "sort";

    // Shared numeric constants
    private static final int SPLIT_LIMIT_TWO = 2;
    private static final int ONE_BASED_OFFSET = 1;
    private static final int LAST_INDEX_OFFSET = 1;

    /**
     * Parses the given user input and executes the corresponding command on the provided tasks.
     *
     * <p>This method recognizes commands such as adding todos, deadlines and events,
     * listing tasks, marking/unmarking, deleting, finding tasks and sorting the list.
     * It trims the input, routes the command to the appropriate handler, and uses {@link Ui}
     * for user output.
     *
     * @param input the raw user input string (may be null)
     * @param tasks the task list to operate on
     * @param ui    the user interface used to present results or feedback
     * @return true if the command indicates the application should exit (e.g. "bye"),
     *         otherwise false
     * @throws maybeweijunException if the input is recognized but invalid for a specific command
     */
    public static boolean process(String input, TaskList tasks, Ui ui) throws maybeweijunException {
        if (input == null) {
            return false;
        }
        input = input.trim();

        if (input.equalsIgnoreCase(CMD_BYE)) {
            return true;
        } else if (input.equalsIgnoreCase(CMD_LIST)) {
            printTaskList(tasks, ui);
        } else if (input.equalsIgnoreCase(CMD_SORT)) {
            printSortedTaskList(tasks, ui);
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

    /**
     * Prints a new TaskList sorted by task description (case-insensitive).
     *
     * <p>A shallow copy of the original list is created and sorted by {@link Task#getDescription()}
     * using a case-insensitive comparator so the original list order remains unchanged.
     * The resulting sorted list is printed via {@link Ui#printTaskList(TaskList)}.
     *
     * @param tasks the task list to sort and print
     * @param ui    the user interface used to print the sorted list
     */
    private static void printSortedTaskList(TaskList tasks, Ui ui) throws maybeweijunException {
        if (tasks.size() == 0) {
            throw new maybeweijunException.EmptyListException();
        }
        List<Task> copy = new ArrayList<>(tasks.toList());
        copy.sort(Comparator.comparingInt(Parser::categoryRank));
        ui.printTaskList(new TaskList(copy));
    }

    /**
     * Sorts the provided task list by category and prints the sorted list via {@link Ui}.
     *
     * <p>A shallow copy of the list is created and sorted using {@link #categoryRank(Task)}
     * so the original list order is not modified.
     *
     * @param tasks the task list to sort and print
     * @param ui    the user interface used to print the resulting list
     */
    private static void printTaskList(TaskList tasks, Ui ui) throws maybeweijunException {
        if (tasks.size() == 0) {
            throw new maybeweijunException.EmptyListException();
        }
        ui.printTaskList(tasks);
    }

    /**
     * Returns an integer rank representing the category of the given task for sorting.
     *
     * <p>Todos are ranked lowest (0), deadlines next (1), events after that (2),
     * and all other task types receive {@link Integer#MAX_VALUE}.
     *
     * @param t the task whose category rank is requested
     * @return an integer rank used for ordering tasks by category
     */
    private static int categoryRank(Task t) {
        if (t instanceof Todo) return 0;
        if (t instanceof Deadline) return 1;
        if (t instanceof Event) return 2;
        return Integer.MAX_VALUE;
    }

    /**
     * Parses a "mark" command from the input and marks the referenced task as done.
     *
     * <p>The command is expected to contain a one-based index after the "mark " prefix.
     * If parsing fails or the index is out of range, the appropriate custom exception is thrown.
     *
     * @param tasks the task list containing the task to mark
     * @param input the full user input starting with the "mark " prefix
     * @param ui    the user interface used to display the marked task
     * @throws maybeweijunException when the index is invalid or cannot be parsed
     */
    private static void handleMark(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        if (tasks.size() == 0) {
            throw new maybeweijunException.EmptyListException();
        }
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

    /**
     * Parses an "unmark" command from the input and marks the referenced task as not done.
     *
     * <p>The command is expected to contain a one-based index after the "unmark " prefix.
     * If parsing fails or the index is out of range, the appropriate custom exception is thrown.
     *
     * @param tasks the task list containing the task to unmark
     * @param input the full user input starting with the "unmark " prefix
     * @param ui    the user interface used to display the unmarked task
     * @throws maybeweijunException when the index is invalid or cannot be parsed
     */
    private static void handleUnmark(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        if (tasks.size() == 0) {
            throw new maybeweijunException.EmptyListException();
        }
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

    /**
     * Parses a "delete" command from the input and removes the referenced task.
     *
     * <p>The command is expected to contain a one-based index after the "delete " prefix.
     * If parsing fails or the index is invalid, the appropriate custom exception is thrown.
     * Successful deletion results in a call to {@link Ui#printDeleted(Task, int)}.
     *
     * @param tasks the task list from which to remove the task
     * @param input the full user input starting with the "delete " prefix
     * @param ui    the user interface used to report the deletion
     * @throws maybeweijunException when the index is invalid or cannot be parsed
     */
    private static void handleDelete(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        if (tasks.size() == 0) {
            throw new maybeweijunException.EmptyListException();
        }
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
     * Finds tasks whose descriptions contain the given keyword and prints them.
     *
     * <p>The search is case-insensitive and matches any substring in the task descriptions.
     * If the search keyword is empty, an {@code EmptyFindException} is thrown.
     *
     * @param tasks the task list to search
     * @param input the full user input starting with the "find " prefix
     * @param ui    the user interface used to print the matching tasks
     * @throws maybeweijunException when the search keyword is empty
     */
    private static void handleFind(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        if (tasks.size() == 0) {
            throw new maybeweijunException.EmptyListException();
        }
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

    /**
     * Parses a "todo" command, validates the description, and adds a new Todo to the list.
     *
     * <p>If the description is empty after the "todo" prefix, an {@code EmptyTodoException}
     * is thrown. On success, the newly added task is printed.
     *
     * @param tasks the task list to which the new Todo will be added
     * @param input the full user input starting with the "todo" prefix
     * @param ui    the user interface used to print the added task
     * @throws maybeweijunException when the todo description is empty
     */
    private static void handleTodo(TaskList tasks, String input, Ui ui) throws maybeweijunException {
        String description = input.substring(CMD_TODO.length()).trim();
        if (description.isEmpty()) {
            throw new maybeweijunException.EmptyTodoException();
        }
        tasks.add(new Todo(description));
        printTaskAdded(tasks, ui);
    }

    /**
     * Parses a "deadline" command, validates description and date, and adds a new Deadline.
     *
     * <p>The input after the "deadline" prefix is split on {@code "/by"} into description
     * and datetime parts. The datetime must parse using the {@code FORMATTER} pattern
     * ({@code yyyy-MM-dd HHmm}); otherwise {@code InvalidDateTimeException} is thrown.
     *
     * @param tasks the task list to which the new Deadline will be added
     * @param input the full user input starting with the "deadline" prefix
     * @param ui    the user interface used to print the added task
     * @throws maybeweijunException when the description or date is empty or the date is invalid
     */
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

    /**
     * Parses an "event" command, validates description and start/end datetimes, and adds a new Event.
     *
     * <p>The input after the "event" prefix is split on {@code "/from"} and {@code "/to"} to obtain
     * the description, start and end datetimes. Datetimes must parse with {@code FORMATTER}, and
     * the end must be strictly after the start; otherwise the corresponding exception is thrown.
     *
     * @param tasks the task list to which the new Event will be added
     * @param input the full user input starting with the "event" prefix
     * @param ui    the user interface used to print the added task
     * @throws maybeweijunException when description or datetimes are empty, invalid, or out of range
     */
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

    /**
     * Prints the most recently added task from the provided task list.
     *
     * <p>The last element is determined using a constant offset to support the one-based
     * indexing used for user-visible messages.
     *
     * @param tasks the task list containing the newly added task
     * @param ui    the user interface used to print the added task
     */
    private static void printTaskAdded(TaskList tasks, Ui ui) {
        ui.printTaskAdded(tasks.get(tasks.size() - LAST_INDEX_OFFSET));
    }
}
