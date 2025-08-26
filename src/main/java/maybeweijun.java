import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class maybeweijun {
    private static final Storage STORAGE = new Storage("state.txt");
    public static void main(String[] args) {
        printLogo();
        printQuery();
        handleUserInput();
    }

    private static void printLogo() {
        Ui.printLogo();
    }

    private static void printQuery() {
        Ui.printQuery();
    }

    private static void handleUserInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        TaskList tasks = new TaskList(STORAGE.load());
        while (true) {
            try {
                String input = reader.readLine();
                if (input == null) break;
                input = input.trim();

                try {
                    boolean shouldExit = Parser.process(input, tasks);
                    if (shouldExit) {
                        exit();
                        break;
                    }
                    saveState(tasks);
                } catch (Exception e) {
                    Ui.printError(e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
                break;
            }
        }
    }


    private static void exit() {
        Ui.printExit();
    }

    public static void saveState(TaskList tasks) {
        STORAGE.save(tasks.toList());
    }

    public static TaskList loadState() {
        return new TaskList(STORAGE.load());
    }
}


