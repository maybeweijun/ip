package maybeweijun.ui;

import maybeweijun.task.Task;
import maybeweijun.task.TaskList;

/**
 * Ui handles all user-facing console output for the application.
 * It formats and prints messages for task operations and general prompts.
 */
public class Ui {
    /**
     * Prints the application logo and a greeting.
     */
    public void printLogo() {

        System.out.println("Bow before Muzan, and I will keep track of your tasks.\n");
    }

    /**
     * Prints the initial prompt asking for user input.
     */
    public void printQuery() {
        System.out.println("What can I do for you? \n");
    }

    /**
     * Prints the list of tasks with their 1-based indices.
     *
     * @param tasks the current list of tasks
     */
    public void printTaskList(TaskList tasks) {
        System.out.println("-----------");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        System.out.println("-----------\n");
    }

    /**
     * Prints feedback indicating a task was added.
     *
     * @param task the task that was added
     */
    public void printTaskAdded(Task task) {
        System.out.println("-----------\nadded: " + task + "\n-----------\n");
    }

    /**
     * Prints feedback indicating a task was marked as done.
     *
     * @param oneBasedIndex the task index shown to the user
     * @param task          the affected task
     */
    public void printMarked(int oneBasedIndex, Task task) {
        System.out.println("Marked task " + oneBasedIndex + " as done.");
        System.out.println(task);
    }

    /**
     * Prints feedback indicating a task was unmarked.
     *
     * @param oneBasedIndex the task index shown to the user
     * @param task          the affected task
     */
    public void printUnmarked(int oneBasedIndex, Task task) {
        System.out.println("Unmarked task " + oneBasedIndex + ".");
        System.out.println(task);
    }

    /**
     * Prints feedback indicating a task was deleted and the remaining count.
     *
     * @param removedTask    the task that was removed
     * @param remainingCount the remaining number of tasks
     */
    public void printDeleted(Task removedTask, int remainingCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(removedTask);
        System.out.println("Now you have " + remainingCount + " tasks in the list.");
    }

    /**
     * Prints the exit message.
     */
    public void printExit() {
        System.out.println("Bye. Hope to see you again soon!\n");
    }

    /**
     * Prints an error message.
     *
     * @param message the error message to print
     */
    public void printError(String message) {
        System.out.println(message);
    }
}
