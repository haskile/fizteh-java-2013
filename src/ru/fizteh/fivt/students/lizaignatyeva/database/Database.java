package ru.fizteh.fivt.students.lizaignatyeva.database;


public class Database {
    public MyTable currentTable;
    public MyTableProvider tableProvider;

    public Database(MyTableProvider tableProvider) {
        this.tableProvider = tableProvider;
        this.currentTable = null;
    }

    public boolean checkActive() {
        boolean result = currentTable != null;
        if (!result) {
            System.out.println("no table");
        }
        return result;
    }
}
