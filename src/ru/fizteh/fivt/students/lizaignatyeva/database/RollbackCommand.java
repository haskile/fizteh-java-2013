package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class RollbackCommand extends Command {
    public RollbackCommand() {
        name = "rollback";
        argumentsAmount = 0;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        Table table = DbMain.getCurrentTable();
        int diff = table.getDifference();
        table.data = table.backup;
        table.saved = true;
        System.out.println(diff);

    }
}
