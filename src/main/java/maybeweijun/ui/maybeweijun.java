package maybeweijun.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import maybeweijun.parser.Parser;
import maybeweijun.storage.Storage;
import maybeweijun.task.TaskList;

public class maybeweijun {
    private static final Storage STORAGE = new Storage("src/main/java/maybeweijun/storage/state.txt");
    private static final Ui UI = new Ui();
    private static final TaskList tasks = new TaskList(STORAGE.load());

    public static void main(String[] args) {
        printLogo();
        printQuery();
        handleUserInput();
    }

    private static void printLogo() {
        UI.printLogo();
    }

    private static void printQuery() {
        UI.printQuery();
    }

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


    private static void exit() {
        UI.printExit();
    }

    public static void saveState(TaskList tasks) {
        STORAGE.save(tasks.toList());
    }

    public static TaskList loadState() {
        return new TaskList(STORAGE.load());
    }
}


