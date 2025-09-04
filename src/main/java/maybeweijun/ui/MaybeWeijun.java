package maybeweijun.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import javafx.fxml.FXMLLoader;


import maybeweijun.parser.Parser;
import maybeweijun.storage.Storage;
import maybeweijun.task.TaskList;

/**
 * Entry point for the application. Initializes storage, ui, and task list,
 * then reads user input in a loop and delegates command processing to the parser.
 */
public class MaybeWeijun extends Application {
    private static final Storage STORAGE = new Storage("src/main/java/maybeweijun/storage/state.txt");
    private static final Ui UI = new Ui();
    private static final TaskList tasks = new TaskList(STORAGE.load());
    private static final String DEFAULT_FILE_PATH = "src/main/java/maybeweijun/storage/state.txt";

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));
    private Duke duke = new Duke();


    /**
     * Starts the application.
     *
     * @param args CLI arguments (unused)
     */
    public static void main(String[] args) {
        // Launch the JavaFX application; GUI will handle user interaction via Parser.
        Application.launch(args);
    }


    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MaybeWeijun.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            // Apply Demon Slayer Infinity Castle background
            scene.getStylesheets().add(MaybeWeijun.class.getResource("/view/demon_slayer.css").toExternalForm());
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setDuke(duke);  // inject the Duke instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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