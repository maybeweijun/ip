import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class maybeweijun {
    public static void main(String[] args) {
        String logo = "[m-a-y-b-e-w-e-i-j-u-n]";
        System.out.println("Hello from\n" + logo + "\n");
        query();
        handleUserInput();
    }

    public static void query() {
        System.out.println("What can I do for you? \n");
    }

    public static void handleUserInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final int MAX_INPUTS = 100;
        Task[] tasks = new Task[MAX_INPUTS];
        int count = 0;
        while (true) {
            try {
                String input = reader.readLine();
                if (input == null) {
                    break;
                }
                if (input.equalsIgnoreCase("bye")) {
                    exit();
                    break;
                }
                if (input.equalsIgnoreCase("list")) {
                    System.out.println("-----------");
                    for (int i = 0; i < count; i++) {
                        System.out.println((i + 1) + ". " + tasks[i]);
                    }
                    System.out.println("-----------\n");
                } else if (input.startsWith("mark ")) {
                    try {
                        int idx = Integer.parseInt(input.substring(5).trim()) - 1;
                        if (idx >= 0 && idx < count && tasks[idx] != null) {
                            tasks[idx].mark();
                            System.out.println("Marked task " + (idx + 1) + " as done.");
                            System.out.println(tasks[idx]);
                        } else {
                            System.out.println("Invalid task number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please provide a valid task number to mark.");
                    }
                } else if (input.startsWith("unmark ")) {
                    try {
                        int idx = Integer.parseInt(input.substring(7).trim()) - 1;
                        if (idx >= 0 && idx < count && tasks[idx] != null) {
                            tasks[idx].unmark();
                            System.out.println("Unmarked task " + (idx + 1) + ".");
                            System.out.println(tasks[idx]);
                        } else {
                            System.out.println("Invalid task number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please provide a valid task number to unmark.");
                    }
                } else {
                    if (count < MAX_INPUTS) {
                        if(input.startsWith("todo")) {
                            String description = input.substring(5).trim();
                            //Converts "todo borrow a book from the library     "
                            //To "borrow a book from the library"
                            tasks[count] = new Todo(description);
                        }
                        else if(input.startsWith("deadline")) {
                            String[] parts = input.substring(9).split("/by");
                            //Split parts into {description, by}
                            if (parts.length == 2) {
                                String description = parts[0].trim();
                                String by = parts[1].trim();
                                tasks[count] = new Deadline(description, by);
                            } else {
                                System.out.println("Invalid deadline format. Use: deadline <description> /by <date>");
                                continue;
                            }

                        }
                        else if(input.startsWith("event")) {
                            // Parse: event <description> /from start_datetime /to end_datetime
                            String[] parts = input.substring(5).split("/from", 2);
                            if (parts.length == 2) {
                                String description = parts[0].trim();
                                String[] timeParts = parts[1].split("/to", 2);
                                if (timeParts.length == 2) {
                                    String start_datetime = timeParts[0].trim();
                                    String end_datetime = timeParts[1].trim();
                                    tasks[count] = new Event(description, start_datetime, end_datetime);
                                } else {
                                    System.out.println("Invalid event format. Use: event <description> /from <start> /to <end>");
                                    continue;
                                }
                            } else {
                                System.out.println("Invalid event format. Use: event <description> /from <start> /to <end>");
                                continue;
                            }
                        } 
                        else {
                            System.out.println("Unknown command. Please use 'todo', 'deadline', 'mark', 'unmark', or 'list'.");
                            continue;
                        }
                        System.out.println("-----------\nadded: " + tasks[count] + "\n-----------\n");
                        count++;
                    } else {
                        System.out.println("Task list is full. Cannot add more tasks.");
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
                break;
            }
        }
    }

    public static void exit() {
        System.out.println("Bye. Hope to see you again soon!\n");
    }
}


