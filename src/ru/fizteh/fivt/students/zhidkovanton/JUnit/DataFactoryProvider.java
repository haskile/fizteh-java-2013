package ru.fizteh.fivt.students.zhidkovanton.JUnit;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.File;

public class DataFactoryProvider implements TableProviderFactory {

    private void checkName(String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException();
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+") || name.contains(File.separator)
                || name.contains(".")) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public DataFactory create(String dir) {
        checkName(dir);
        return new DataFactory(dir);
    }
}
