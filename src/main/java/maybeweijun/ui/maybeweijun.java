package maybeweijun.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import maybeweijun.parser.Parser;
import maybeweijun.storage.Storage;
import maybeweijun.task.TaskList;

/**
 * Entry point for the application. Initializes storage, ui, and task list,
 * then reads user input in a loop and delegates command processing to the parser.
 */
public class MaybeWeijun {
    private static final Storage STORAGE = new Storage("src/main/java/maybeweijun/storage/state.txt");
    private static final Ui UI = new Ui();
    private static final TaskList tasks = new TaskList(STORAGE.load());

    /**
     * Starts the application.
     *
     * @param args CLI arguments (unused)
     */
    public static void main(String[] args) {
        printLogo();
        printQuery();
        handleUserInput();
    }

    /**
     * Prints the application logo via the UI.
     */
    private static void printLogo() {
        UI.printLogo();
    }

    /**
     * Prints the query/prompt via the UI.
     */
    private static void printQuery() {
        UI.printQuery();
    }

    /**
     * Reads input lines from stdin and dispatches them to the parser until exit is requested
     * or an IO error occurs.
     */
    private static void handleUserInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String input = reader.readLine();
                if (input == null) break;
                input = input.trim();

                try {
                    boolean shouldExit = Parser.process(input, tasks, UI);
                    if (shouldExit) {
                        exit();
                        break;
                    }
                    saveState(tasks);
                } catch (Exception e) {
                    UI.printError(e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
                break;
            }
        }
    }

    /**
     * Prints the exit message via the UI.
     */
    private static void exit() {
        UI.printExit();
    }

    /**
     * Persists the current state of tasks to storage.
     *
     * @param tasks the task list to save
     */
    public static void saveState(TaskList tasks) {
        STORAGE.save(tasks.toList());
    }

    /**
     * Loads tasks from storage into a new TaskList.
     *
     * @return a new TaskList populated from storage
     */
    public static TaskList loadState() {
        return new TaskList(STORAGE.load());
    }
}