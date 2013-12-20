package ru.fizteh.fivt.students.lizaignatyeva.database.commands;

import ru.fizteh.fivt.students.lizaignatyeva.database.Database;
import ru.fizteh.fivt.students.lizaignatyeva.database.DbMain;
import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class ExitCommand extends Command {
    private Database database;

    public ExitCommand(Database database) {
        this.database = database;
        name = "exit";
        argumentsAmount = 0;
    }

    @Override
    public void run(String[] args) throws Exception {
        try {
            database.currentTable.write();
        } catch (Exception e) {
            System.err.println("Ooops! Error: " + e.getMessage());
        }
        System.exit(0);
    }
}
