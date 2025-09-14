package maybeweijun.ui;

import maybeweijun.task.Task;
import maybeweijun.task.TaskList;

/**
 * Collects output into an internal buffer for GUI display instead of printing to the console.
 */
public class GuiUi extends Ui {
    private final StringBuilder buffer = new StringBuilder();

    public void clear() {
        buffer.setLength(0);
    }

    public String consume() {
        String out = buffer.toString();
        buffer.setLength(0);
        return out;
    }

    @Override
    public void printTaskList(TaskList tasks) {
        buffer.append("-----------\n");
        for (int i = 0; i < tasks.size(); i++) {
            buffer.append(i + 1).append(". ").append(tasks.get(i)).append('\n');
        }
        buffer.append("-----------\n\n");
    }

    @Override
    public void printTaskAdded(Task task) {
        buffer.append("-----------\nadded: ")
              .append(task)
              .append("\n-----------\n\n");
    }

    @Override
    public void printMarked(int oneBasedIndex, Task task) {
        buffer.append("Marked task ")
              .append(oneBasedIndex)
              .append(" as done.\n")
              .append(task)
              .append('\n');
    }

    @Override
    public void printUnmarked(int oneBasedIndex, Task task) {
        buffer.append("Unmarked task ")
              .append(oneBasedIndex)
              .append(".\n")
              .append(task)
              .append('\n');
    }

    @Override
    public void printDeleted(Task removedTask, int remainingCount) {
        buffer.append("Noted. I've removed this task:\n")
              .append(removedTask)
              .append('\n')
              .append("Now you have ")
              .append(remainingCount)
              .append(" tasks in the list.\n");
    }


    @Override
    public void printError(String message) {
        buffer.append(message).append('\n');
    }
}
