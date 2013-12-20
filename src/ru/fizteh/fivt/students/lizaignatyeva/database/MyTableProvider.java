package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MyTableProvider implements TableProvider {
    private Path directory;

    private boolean isValidTableName(String name) {
        return name != null;
    }

    public MyTableProvider(Path directory) {
        this.directory = directory;
    }

    @Override
    public MyTable getTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("TableProvider.getTable: name '" + name + "' is illegal");
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
        File path = directory.resolve(name).toFile();
        FileUtils.remove(path);
    }
}
