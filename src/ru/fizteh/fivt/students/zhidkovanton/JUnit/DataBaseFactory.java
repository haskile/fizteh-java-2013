package ru.fizteh.fivt.students.zhidkovanton.JUnit;

public class DataBaseFactory {
    public DataBase dataBase = null;
    public DataFactory dataFactory;

    public DataBaseFactory(String directory) {
        dataFactory = new DataFactory(directory);
    }
}
