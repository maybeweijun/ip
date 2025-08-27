package maybeweijun.task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoTest {
    @Test
    public void constructorTest() {
        Todo t1 = new Todo("test");
        assertEquals("test", t1.getDescription());
    }

    @Test
    public void descriptionTest() {
        Todo t1 = new Todo("test");
        assertEquals("test", t1.getDescription());
    }


}