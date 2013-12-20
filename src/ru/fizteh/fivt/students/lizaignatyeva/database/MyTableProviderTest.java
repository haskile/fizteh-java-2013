package ru.fizteh.fivt.students.lizaignatyeva.database;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.Assert.assertEquals;

public class MyTableProviderTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private MyTableProvider tableProvider;

    @Before
    public void createTableProvider() {
        tableProvider = new MyTableProvider(folder.getRoot().toPath());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetNullArgument() {
        tableProvider.getTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNullArgument() {
        tableProvider.createTable(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveNullArgument() {
        tableProvider.removeTable(null);
    }

    @Test (expected = IllegalStateException.class)
    public void testRemoveNonExistingTable() {
        tableProvider.removeTable("NoSuchTable");
    }

    @Test
    public void testBasics() {
        MyTable table = tableProvider.createTable("table1");
        assertEquals(tableProvider.createTable("table1"), null);
        assertEquals(tableProvider.getTable("table1"), table);
        tableProvider.removeTable("table1");
        assertEquals(tableProvider.getTable("table1"), null);
    }

    @Test
    public void isSameObject() {
        tableProvider.createTable("table");
        MyTable table1 = tableProvider.getTable("table");
        MyTable table2 = tableProvider.getTable("table");
        assertEquals(table1, table2);
    }
}
