package ru.fizteh.fivt.students.lizaignatyeva.database.commands;

import ru.fizteh.fivt.students.lizaignatyeva.database.Database;
import ru.fizteh.fivt.students.lizaignatyeva.database.MyTable;
import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class CreateCommand extends Command {
    private Database database;

    public CreateCommand(Database database) {
        this.database = database;
        name = "create";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        String tableName = args[0];
        MyTable table = database.tableProvider.createTable(tableName);
        if (table == null) {
            System.out.println("tablename exists");
        } else {
            System.out.println("created");
        }
    }
}
