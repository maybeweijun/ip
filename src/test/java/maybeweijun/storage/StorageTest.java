package maybeweijun.storage;

import maybeweijun.task.Deadline;
import maybeweijun.task.Event;
import maybeweijun.task.Task;
import maybeweijun.task.Todo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @TempDir
    Path tempDir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    @Test
    void saveAndLoad_roundTrip_preservesAllFields() throws Exception {
        Path file = tempDir.resolve("state.txt");
        Storage storage = new Storage(file.toString());

        List<Task> tasks = new ArrayList<>();
        Todo t1 = new Todo("buy milk");
        t1.mark();
        tasks.add(t1);

        Deadline d1 = new Deadline("submit report", "2025-01-02 1230");
        tasks.add(d1);

        Event e1 = new Event("meeting", "2025-01-03 0900", "2025-01-03 1000");
        e1.mark();
        tasks.add(e1);

        storage.save(tasks);

        Storage storage2 = new Storage(file.toString());
        List<Task> loaded = storage2.load();

        assertEquals(3, loaded.size());

        assertInstanceOf(Todo.class, loaded.get(0));
        assertTrue(loaded.get(0).isDone());
        assertEquals("buy milk", loaded.get(0).getDescription());

        assertInstanceOf(Deadline.class, loaded.get(1));
        Deadline loadedDeadline = (Deadline) loaded.get(1);
        assertFalse(loadedDeadline.isDone());
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDateTime.parse("2025-01-02 1230", FORMATTER), loadedDeadline.getBy());

        assertInstanceOf(Event.class, loaded.get(2));
        Event loadedEvent = (Event) loaded.get(2);
        assertTrue(loadedEvent.isDone());
        assertEquals("meeting", loadedEvent.getDescription());
        assertEquals(LocalDateTime.parse("2025-01-03 0900", FORMATTER), loadedEvent.getFrom());
        assertEquals(LocalDateTime.parse("2025-01-03 1000", FORMATTER), loadedEvent.getTo());
    }

    @Test
    void save_overwritesExistingFile() throws Exception {
        Path file = tempDir.resolve("state_overwrite.txt");
        Files.write(file, List.of("garbage line 1", "garbage line 2"), StandardCharsets.UTF_8);

        Storage storage = new Storage(file.toString());
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("hello"));
        storage.save(tasks);

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
        assertEquals("T | 0 | hello", lines.get(0));
    }

    @Test
    void load_nonExistentFile_returnsEmptyList() {
        Path file = tempDir.resolve("does_not_exist.txt");
        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_skipsMalformedLinesButKeepsValidOnes() throws Exception {
        Path file = tempDir.resolve("mixed.txt");
        List<String> lines = List.of(
                "X | 1 | unknown type",
                "T | 0 | todo ok",
                "D | 1 | deadline missing by",
                "E | 0 | event missing times",
                "E | 1 | event ok | 2025-02-01 0800 to 2025-02-01 0900",
                "D | 0 | deadline ok | 2025-02-03 1800"
        );
        Files.write(file, lines, StandardCharsets.UTF_8);

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());

        assertInstanceOf(Todo.class, loaded.get(0));
        assertEquals("todo ok", loaded.get(0).getDescription());

        assertInstanceOf(Event.class, loaded.get(1));
        assertEquals("event ok", loaded.get(1).getDescription());

        assertInstanceOf(Deadline.class, loaded.get(2));
        assertEquals("deadline ok", loaded.get(2).getDescription());
    }

    @Test
    void loadOrThrow_invalidDate_throwsInvalidStorageFormatException() throws Exception {
        Path file = tempDir.resolve("bad_date.txt");
        List<String> lines = List.of(
                "D | 0 | bad deadline | 2025-99-01 1800"
        );
        Files.write(file, lines, StandardCharsets.UTF_8);

        Storage storage = new Storage(file.toString());
        assertThrows(maybeweijunException.InvalidStorageFormatException.class, storage::loadOrThrow);
    }

    @Test
    void saveOrThrow_whenPathIsDirectory_throwsStorageSaveException() {
        Storage storage = new Storage(tempDir.toString());
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("x"));

        assertThrows(maybeweijunException.StorageSaveException.class, () -> storage.saveOrThrow(tasks));
    }
}