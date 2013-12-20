package ru.fizteh.fivt.students.lizaignatyeva.database;

import org.junit.Before;
import org.junit.Test;

public class MyTableProviderFactoryTest {
    private MyTableProviderFactory tableProviderFactory;

    @Before
    public void createProvider() {
        tableProviderFactory = new MyTableProviderFactory();
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullArgument() {
        tableProviderFactory.create(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBadDirectory() {
        tableProviderFactory.create("/there/is/no/such/directory");
    }
}
