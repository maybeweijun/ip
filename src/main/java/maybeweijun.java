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
        System.out.println("What can I do for you?\n");
    }

    public static void handleUserInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final int MAX_INPUTS = 100;
        String[] inputs = new String[MAX_INPUTS];
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
                        System.out.println(i+1 + ". " + inputs[i]);
                    }
                    System.out.println("-----------\n");
                } else {
                    if (count < MAX_INPUTS) {
                        inputs[count] = input;
                        count++;
                    }
                    System.out.println("-----------\nadded: " + input + "\n-----------\n");
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

