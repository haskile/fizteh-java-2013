package ru.fizteh.fivt.students.zhidkovanton.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import  ru.fizteh.fivt.storage.strings.TableProvider;

public class DataBaseFactory {
    public DataBase dataBase = null;
    public DataFactory dataFactory;

    public DataBaseFactory(String directory) {
        dataFactory = new DataFactory(directory);
    }
}
