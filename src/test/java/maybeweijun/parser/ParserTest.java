package maybeweijun.parser;

import maybeweijun.exception.MaybeWeijunException;
import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.TaskList;
import maybeweijun.task.Todo;
import maybeweijun.ui.Ui;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private static class FakeUi extends Ui {
        boolean printedTaskList = false;
        TaskList lastTaskList;

        boolean printedTaskAdded = false;
        Task lastAddedTask;

        boolean printedMarked = false;
        int lastMarkedIndex = -1;
        Task lastMarkedTask;

        boolean printedUnmarked = false;
        int lastUnmarkedIndex = -1;
        Task lastUnmarkedTask;

        boolean printedDeleted = false;
        Task lastDeletedTask;
        int lastRemainingCount = -1;

        @Override
        public void printTaskList(TaskList tasks) {
            this.printedTaskList = true;
            this.lastTaskList = tasks;
        }

        @Override
        public void printTaskAdded(Task task) {
            this.printedTaskAdded = true;
            this.lastAddedTask = task;
        }

        @Override
        public void printMarked(int oneBasedIndex, Task task) {
            this.printedMarked = true;
            this.lastMarkedIndex = oneBasedIndex;
            this.lastMarkedTask = task;
        }

        @Override
        public void printUnmarked(int oneBasedIndex, Task task) {
            this.printedUnmarked = true;
            this.lastUnmarkedIndex = oneBasedIndex;
            this.lastUnmarkedTask = task;
        }

        @Override
        public void printDeleted(Task removedTask, int remainingCount) {
            this.printedDeleted = true;
            this.lastDeletedTask = removedTask;
            this.lastRemainingCount = remainingCount;
        }
    }

    private TaskList tasks;
    private FakeUi ui;

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
        ui = new FakeUi();
    }

    @Test
    void process_nullInput_returnsFalse() throws Exception {
        assertFalse(Parser.process(null, tasks, ui));
    }

    @Test
    void process_bye_returnsTrue() throws Exception {
        assertTrue(Parser.process("bye", tasks, ui));
    }

    @Test
    void process_list_printsTaskList() throws Exception {
        tasks.add(new Todo("a"));
        assertFalse(Parser.process("list", tasks, ui));
        assertTrue(ui.printedTaskList);
        assertSame(tasks, ui.lastTaskList);
    }

    @Test
    void process_todo_onlyKeyword_throwsOnlyTodo() {
        assertThrows(MaybeWeijunException.OnlyTodoException.class,
                () -> Parser.process("todo", tasks, ui));
    }


    @Test
    void process_todo_valid_addsTaskAndPrintsAdded() throws Exception {
        assertFalse(Parser.process("todo buy milk", tasks, ui));
        assertEquals(1, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("buy milk", tasks.get(0).getDescription());
        assertTrue(ui.printedTaskAdded);
        assertSame(tasks.get(0), ui.lastAddedTask);
    }

    @Test
    void process_deadline_onlyKeyword_throwsOnlyDeadline() {
        assertThrows(MaybeWeijunException.OnlyDeadlineException.class,
                () -> Parser.process("deadline", tasks, ui));
    }

    @Test
    void process_deadline_missingBy_throwsEmptyDeadline() {
        assertThrows(MaybeWeijunException.EmptyDeadlineException.class,
                () -> Parser.process("deadline finish report", tasks, ui));
    }

    @Test
    void process_deadline_emptyParts_throwsEmptyDeadline() {
        assertThrows(MaybeWeijunException.EmptyDeadlineException.class,
                () -> Parser.process("deadline    /by   ", tasks, ui));
    }

    @Test
    void process_deadline_invalidDate_throwsInvalidDateTime() {
        assertThrows(MaybeWeijunException.InvalidDateTimeException.class,
                () -> Parser.process("deadline finish /by 2023-13-01 2500", tasks, ui));
    }

    @Test
    void process_deadline_valid_addsTaskAndPrintsAdded() throws Exception {
        assertFalse(Parser.process("deadline finish report /by 2025-01-01 1800", tasks, ui));
        assertEquals(1, tasks.size());
        assertInstanceOf(Deadline.class, tasks.get(0));
        assertTrue(ui.printedTaskAdded);
        assertSame(tasks.get(0), ui.lastAddedTask);
    }

    @Test
    void process_event_onlyKeyword_throwsOnlyEvent() {
        assertThrows(MaybeWeijunException.OnlyEventException.class,
                () -> Parser.process("event", tasks, ui));
    }

    @Test
    void process_event_missingFromTo_throwsEmptyEvent() {
        assertThrows(MaybeWeijunException.EmptyEventException.class,
                () -> Parser.process("event meeting", tasks, ui));
        assertThrows(MaybeWeijunException.EmptyEventException.class,
                () -> Parser.process("event meeting /from 2025-01-01 0900", tasks, ui));
        assertThrows(MaybeWeijunException.EmptyEventException.class,
                () -> Parser.process("event  /from 2025-01-01 0900 /to 2025-01-01 1000", tasks, ui));
    }

    @Test
    void process_event_invalidDate_throwsInvalidDateTime() {
        assertThrows(MaybeWeijunException.InvalidDateTimeException.class,
                () -> Parser.process("event meet /from 2025-99-01 0900 /to 2025-01-01 1000", tasks, ui));
        assertThrows(MaybeWeijunException.InvalidDateTimeException.class,
                () -> Parser.process("event meet /from 2025-01-01 0900 /to 2025-01-01 1060", tasks, ui));
    }

    @Test
    void process_event_valid_addsTaskAndPrintsAdded() throws Exception {
        assertFalse(Parser.process("event meeting /from 2025-01-01 0900 /to 2025-01-01 1000", tasks, ui));
        assertEquals(1, tasks.size());
        assertInstanceOf(Event.class, tasks.get(0));
        assertTrue(ui.printedTaskAdded);
        assertSame(tasks.get(0), ui.lastAddedTask);
    }

    @Test
    void process_mark_valid_marksTaskAndPrints() throws Exception {
        tasks.add(new Todo("x"));
        assertFalse(Parser.process("mark 1", tasks, ui));
        assertTrue(tasks.get(0).isDone());
        assertTrue(ui.printedMarked);
        assertEquals(1, ui.lastMarkedIndex);
        assertSame(tasks.get(0), ui.lastMarkedTask);
    }

    @Test
    void process_mark_invalidIndex_throwsInvalidTaskNumber() {
        assertThrows(MaybeWeijunException.InvalidTaskNumberException.class,
                () -> Parser.process("mark 1", tasks, ui));
    }

    @Test
    void process_mark_badNumber_throwsInvalidMark() {
        assertThrows(MaybeWeijunException.InvalidMarkException.class,
                () -> Parser.process("mark xyz", tasks, ui));
    }

    @Test
    void process_unmark_valid_unmarksTaskAndPrints() throws Exception {
        tasks.add(new Todo("x"));
        Parser.process("mark 1", tasks, ui);
        assertFalse(Parser.process("unmark 1", tasks, ui));
        assertFalse(tasks.get(0).isDone());
        assertTrue(ui.printedUnmarked);
        assertEquals(1, ui.lastUnmarkedIndex);
        assertSame(tasks.get(0), ui.lastUnmarkedTask);
    }

    @Test
    void process_unmark_invalidIndex_throwsInvalidTaskNumber() {
        assertThrows(MaybeWeijunException.InvalidTaskNumberException.class,
                () -> Parser.process("unmark 2", tasks, ui));
    }

    @Test
    void process_unmark_badNumber_throwsInvalidUnmark() {
        assertThrows(MaybeWeijunException.InvalidUnmarkException.class,
                () -> Parser.process("unmark abc", tasks, ui));
    }

    @Test
    void process_delete_valid_removesAndPrints() throws Exception {
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        assertFalse(Parser.process("delete 1", tasks, ui));
        assertEquals(1, tasks.size());
        assertEquals("b", tasks.get(0).getDescription());
        assertTrue(ui.printedDeleted);
        assertEquals(1, ui.lastRemainingCount);
        assertEquals("a", ui.lastDeletedTask.getDescription());
    }

    @Test
    void process_delete_invalidIndex_throwsInvalidTaskNumber() {
        assertThrows(MaybeWeijunException.InvalidTaskNumberException.class,
                () -> Parser.process("delete 3", tasks, ui));
    }

    @Test
    void process_delete_badNumber_throwsInvalidDelete() {
        assertThrows(MaybeWeijunException.InvalidDeleteException.class,
                () -> Parser.process("delete NaN", tasks, ui));
    }

    @Test
    void process_invalidCommand_throwsInvalidCommand() {
        assertThrows(MaybeWeijunException.InvalidCommandException.class,
                () -> Parser.process("unknown", tasks, ui));
    }

    @Test
    void process_event_endBeforeStart_throwsInvalidDateRange() {
        assertThrows(MaybeWeijunException.InvalidDateRangeException.class,
                () -> Parser.process("event bad /from 2025-01-01 1200 /to 2025-01-01 1100", tasks, ui));
    }
}