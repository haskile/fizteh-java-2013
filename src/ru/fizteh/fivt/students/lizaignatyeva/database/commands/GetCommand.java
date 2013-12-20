package ru.fizteh.fivt.students.lizaignatyeva.database.commands;

import ru.fizteh.fivt.students.lizaignatyeva.database.Database;
import ru.fizteh.fivt.students.lizaignatyeva.database.DbMain;
import ru.fizteh.fivt.students.lizaignatyeva.database.backup.MyTable;
import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class GetCommand extends Command {
    private Database database;

    public GetCommand(Database database) {
        this.database = database;
        name = "get";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        String key = args[0];
        if (!database.checkActive()) {
            return;
        }
        try {
            String value = database.currentTable.get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        } catch (Exception e) {
            System.err.println("get: " + e.getMessage());
        }

    }
}
