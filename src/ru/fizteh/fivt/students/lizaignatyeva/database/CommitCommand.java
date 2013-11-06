package ru.fizteh.fivt.students.lizaignatyeva.database;


import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class CommitCommand extends Command {
    public CommitCommand() {
        name = "commit";
        argumentsAmount = 0;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        Table table = DbMain.getCurrentTable();
        int diff = table.getDifference();
        table.backup = table.data;
        table.saved = true;
        System.out.println(diff);

    }
}
