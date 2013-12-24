package ru.fizteh.fivt.students.zhidkovanton.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;


public class DataBaseFactory {
    public DataBase dataBase;
    public DataFactory dataFactory;

    public DataBaseFactory(TableProvider provider) {
        dataFactory = (DataFactory) provider;
        dataBase = null;
    }
}
