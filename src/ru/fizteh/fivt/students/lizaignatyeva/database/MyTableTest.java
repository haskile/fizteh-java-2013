package ru.fizteh.fivt.students.lizaignatyeva.database;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;


public class MyTableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MyTable table;
    private final String tableName = "testTable";

    @Before
    public void createTable() throws IOException {
        table = new MyTable(folder.getRoot().toPath(), tableName);
        table.write();
    }

    @Test
    public void testGetName() {
        assertEquals(tableName, table.getName());
    }

    @Test
    public void testBasics() {
        final String key = "testKey";
        final String oldValue = "old";
        final String newValue = "new";

        assertEquals(table.get(key), null);
        assertEquals(table.put(key, oldValue), null);
        assertEquals(table.get(key), oldValue);
        assertEquals(table.put(key, newValue), oldValue);
        assertEquals(table.remove(key), newValue);
        assertEquals(table.get(key), null);
        assertEquals(table.remove(key), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullArgument() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutFirstNullArgument() {
        table.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutSecondNullArgument() {
        table.put("key", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullArgument() {
        table.remove(null);
    }

    @Test
    public void testSize() {
        assertEquals(table.size(), 0);
        table.put("key1", "value1");
        assertEquals(table.size(), 1);
        table.put("key2", "value2");
        assertEquals(table.size(), 2);
        table.remove("key2");
        assertEquals(table.size(), 1);
        table.remove("key1");
        assertEquals(table.size(), 0);
    }

    @Test
    public void testCommit() {
        table.put("key1", "value1");
        table.put("key2", "value2");
        assertEquals(table.commit(), 2);
        assertEquals(table.size(), 2);
        assertEquals(table.get("key1"), "value1");
        assertEquals(table.get("key2"), "value2");
        table.put("key2", "value3");
        table.remove("key2");
        assertEquals(table.commit(), 1);
        assertEquals(table.size(), 1);
        assertEquals(table.get("key1"), "value1");
        assertEquals(table.get("key2"), null);
    }

    @Test
    public void testRollback() {
        table.put("key1", "value1");
        table.put("key2", "value2");
        assertEquals(table.rollback(), 2);
        table.put("key1", "value1");
        table.put("key2", "value2");
        assertEquals(table.commit(), 2);
        table.remove("key2");
        table.put("key1", "value3");
        assertEquals(table.rollback(), 2);
        assertEquals(table.get("key1"), "value1");
        assertEquals(table.get("key2"), "value2");
    }
}
