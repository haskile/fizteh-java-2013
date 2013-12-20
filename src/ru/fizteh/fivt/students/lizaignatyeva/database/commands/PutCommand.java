package ru.fizteh.fivt.students.lizaignatyeva.database.commands;

import ru.fizteh.fivt.students.lizaignatyeva.database.Database;
import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class PutCommand extends Command {
    private Database database;

    public PutCommand(Database database) {
        this.database = database;
        name = "put";
        argumentsAmount = 2;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (database.checkActive()) {
            String key = args[0];
            String value = args[1];
            try {
                String oldValue = database.currentTable.put(key, value);
                if (oldValue == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(oldValue);
                }
            } catch (Exception e) {
                //cry and do nothing
            }
        }
    }
}
