package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class MyTableProvider implements TableProvider {
    private Path directory;

    private HashMap<String, MyTable> loadedTables;

    private boolean isValidTableName(String name) {
        return name != null && !name.equals("");
    }

    public MyTableProvider(Path directory) {
        this.directory = directory;
        this.loadedTables = new HashMap<>();
    }

    @Override
    public MyTable getTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("TableProvider.getTable: name '" + name + "' is illegal");
        }
        if (loadedTables.containsKey(name)) {
            return loadedTables.get(name);
        }
        MyTable table = new MyTable(directory, name);
        if (!table.exists()) {
            return null;
        }
        try {
            table.read();
        } catch (Exception e) {
            return null;
        }
        return table;
    }

    @Override
    public MyTable createTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("TableProvider.createTable: name '" + name + "' is illegal");
        }
        MyTable table = new MyTable(directory, name);
        if (table.exists()) {
            return null;
        }
        try {
            table.write();
        } catch (IOException e) {
            throw new IllegalArgumentException("TableProvider.createTable: name '" + name + "' failed: IO failure");
        }
        loadedTables.put(name, table);
        return table;
    }

    @Override
    public void removeTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("TableProvider.removeTable: name '" + name + "' is illegal");
        }
        MyTable table = new MyTable(directory, name);
        if (!table.exists()) {
            throw new IllegalStateException("TableProvider.removeTable: table '" + name + "' does not exist");
        }
        if (loadedTables.containsKey(name)) {
            loadedTables.remove(name);
        }
        File path = directory.resolve(name).toFile();
        FileUtils.remove(path);
    }
}
