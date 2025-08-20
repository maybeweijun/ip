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
                System.out.println("-----------\n"+input+"\n-----------\n");
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

