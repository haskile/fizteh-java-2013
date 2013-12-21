package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DataFormatException;

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
    public MyTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("TableProvider.createTable: name '" + name + "' is illegal");
        }
        if (columnTypes == null) {
            throw new IllegalArgumentException("TableProvider.createTable: null columnTypes is illegal");
        }
        for (Class clazz : columnTypes) {
            if (clazz == null) {
                throw new IllegalArgumentException("TableProvider.createTable: null column type");
            }
        }
        StoreableSignature storeableSignature = new StoreableSignature(columnTypes);
        MyTable table = new MyTable(directory, name, storeableSignature, this);
        if (MyTable.exists(directory, name)) {
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
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (table == null || value == null) {
            throw new IllegalArgumentException("TableProvider.deserialize: null arguments are not supported")
        }

        MyTable myTable = (MyTable) table;
        MyStoreable storeable = new MyStoreable(myTable.columnTypes);
        storeable.deserialize(value);
        return storeable;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (table == null || value == null) {
            throw new IllegalArgumentException("TableProvider.serialize: null arguments are not supported")
        }
        return ((MyStoreable) value).serialize();
    }

    @Override
    public Storeable createFor(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("TableProvider.createFor: null arguments are not supported")
        }
        MyTable myTable = (MyTable) table;
        return new MyStoreable(myTable.columnTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table == null || values == null) {
            throw new IllegalArgumentException("TableProvider.createFor: null arguments are not supported")
        }
        MyTable myTable;
        myTable = (MyTable) table;
        MyStoreable storeable = new MyStoreable(myTable.columnTypes);
        for (int index = 0; index < values.size(); index ++) {
            storeable.setColumnAt(index, values.get(index));
        }
        return storeable;
    }

    @Override
    public MyTable getTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("TableProvider.getTable: name '" + name + "' is illegal");
        }
        if (loadedTables.containsKey(name)) {
            return loadedTables.get(name);
        }
        if (!MyTable.exists(directory, name)) {
            return null;
        }
        MyTable table;
        try {
            table = MyTable.read(directory, name, this);
        } catch (DataFormatException e) {
            throw new IllegalArgumentException("Broken table found");
        } catch (IOException e) {
            throw new RuntimeException("IOFailure happened during table reading");
        } catch (Exception e) {
            return null;
        }
        return table;
    }

    @Override
    public void removeTable(String name) {
        if (!isValidTableName(name)) {
            throw new IllegalArgumentException("TableProvider.removeTable: name '" + name + "' is illegal");
        }
        if (!MyTable.exists(directory, name)) {
            throw new IllegalStateException("TableProvider.removeTable: table '" + name + "' does not exist");
        }
        if (loadedTables.containsKey(name)) {
            loadedTables.get(name).markAsDeleted();
            loadedTables.remove(name);
        }
        File path = directory.resolve(name).toFile();
        FileUtils.remove(path);
    }
}
