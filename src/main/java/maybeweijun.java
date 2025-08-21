import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
        ArrayList<Task> tasks = new ArrayList<>();
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
                } catch (maybeweijunException e) {
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
}


