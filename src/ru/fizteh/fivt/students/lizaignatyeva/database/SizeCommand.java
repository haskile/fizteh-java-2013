package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class SizeCommand extends Command {
    public SizeCommand() {
        name = "size";
        argumentsAmount = 0;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        Table table = DbMain.getCurrentTable();
        int size = table.getSize();
        System.out.println(size);
    }
}
