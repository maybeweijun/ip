package maybeweijun.ui;

import maybeweijun.parser.Parser;
import maybeweijun.storage.Storage;
import maybeweijun.task.TaskList;

/**
 * Duke bridges the GUI and the core logic (Parser/TaskList/Storage).
 * It converts user input into responses suitable for display in the GUI.
 */
public class Duke {
    private static final String DEFAULT_FILE_PATH = "src/main/java/maybeweijun/storage/state.txt";


    private final Storage storage;
    private final TaskList tasks;
    private final GuiUi ui;

    private volatile boolean exitRequested = false;

    public Duke() {
        this.storage = new Storage(DEFAULT_FILE_PATH);
        this.tasks = new TaskList(storage.load());
        this.ui = new GuiUi();
    }

    /**
     * Returns the initial greeting shown in the GUI.
     */
    public String getGreeting() {

        return "Bow before Muzan, and I will keep track of your tasks.\n";
    }

    /**
     * Processes user input via Parser and returns the message(s) to show in the GUI.
     * Also persists state after each command and tracks exit requests.
     */
    public String getResponse(String input) {
        ui.clear();
        try {
            boolean shouldExit = Parser.process(input, tasks, ui);
            storage.save(tasks.toList());
            if (shouldExit) {
                exitRequested = true;
            }
        } catch (Exception e) {
            ui.printError(e.getMessage());
        }
        return ui.consume();
    }

    /**
     * Indicates whether the user has requested to exit (e.g., "bye").
     */
    public boolean isExitRequested() {
        return exitRequested;
    }
}
