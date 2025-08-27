package maybeweijun.ui;

import maybeweijun.task.Task;
import maybeweijun.task.TaskList;

public class Ui {
    public void printLogo() {
        String logo = "[m-a-y-b-e-w-e-i-j-u-n]";
        System.out.println("Hello from\n" + logo + "\n");
    }

    public void printQuery() {
        System.out.println("What can I do for you? \n");
    }

    public void printTaskList(TaskList tasks) {
        System.out.println("-----------");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        System.out.println("-----------\n");
    }

    public void printTaskAdded(Task task) {
        System.out.println("-----------\nadded: " + task + "\n-----------\n");
    }

    public void printMarked(int oneBasedIndex, Task task) {
        System.out.println("Marked task " + oneBasedIndex + " as done.");
        System.out.println(task);
    }

    public void printUnmarked(int oneBasedIndex, Task task) {
        System.out.println("Unmarked task " + oneBasedIndex + ".");
        System.out.println(task);
    }

    public void printDeleted(Task removedTask, int remainingCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(removedTask);
        System.out.println("Now you have " + remainingCount + " tasks in the list.");
    }

    public void printExit() {
        System.out.println("Bye. Hope to see you again soon!\n");
    }

    public void printError(String message) {
        System.out.println(message);
    }
}
