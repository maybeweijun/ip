package maybeweijun.ui;

import maybeweijun.parser.Parser;
import maybeweijun.storage.Storage;
import maybeweijun.task.TaskList;
import java.io.File;
import java.io.IOException;

/**
 * Bridges the GUI and core logic, converting user input into responses suitable for display in the GUI.
 */
public class Duke {
    private static final String DEFAULT_FILE_PATH = "data/state.txt";


    private final Storage storage;
    private final TaskList tasks;
    private final GuiUi ui;

    private volatile boolean exitRequested = false;

    public Duke() {
        // Ensure the "data" folder and "state.txt" file exist before using Storage.
        File file = new File(DEFAULT_FILE_PATH);
        File parent = file.getParentFile();
        try {
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            // If creation fails, continue — Storage.load/save are lenient and will handle IO issues.
            // Optionally log the exception here.
        }

        this.storage = new Storage(DEFAULT_FILE_PATH);
        this.tasks = new TaskList(storage.load());
        this.ui = new GuiUi();
    }

    /**
     * Returns the initial greeting shown in the GUI.
     */
    public String getGreeting() {

        return "Bow before MaybeWeijun, and I will keep track of your tasks.\n";
    }

    /**
     * Processes user input via Parser and returns the message(s) to show in the GUI.
     * Also persists state after each command and tracks exit requests.
     */
    public String getResponse(String input) {
        assert ui != null : "Ui must be initialized";
        assert storage != null : "Storage must be initialized";
        assert tasks != null : "TaskList must be initialized";
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
