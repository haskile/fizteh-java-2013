package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MyTableProviderFactory implements TableProviderFactory {
    private boolean isValidName(String name) {
        return name != null;
    }

    @Override
    public MyTableProvider create(String dir) {
        if (!isValidName(dir)) {
            throw new IllegalArgumentException("TableProviderFactory.create: name '" + dir + "' is invalid");
        }
        if (dir.isEmpty()) {
            throw new IllegalArgumentException("TableProviderFactory.create: path is empty");
        }
        Path path = Paths.get(dir);
        if (!path.toFile().isDirectory()) {
            throw new IllegalArgumentException("TableProviderFactory.create: there is no directory " + dir);
        }
        return new MyTableProvider(Paths.get(dir));
    }
}