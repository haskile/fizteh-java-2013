package ru.fizteh.fivt.students.lizaignatyeva.database.commands;

import ru.fizteh.fivt.students.lizaignatyeva.database.Database;
import ru.fizteh.fivt.students.lizaignatyeva.database.MyTable;
import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

import java.util.Arrays;
import java.util.HashMap;

public class CreateCommand extends Command {
    private Database database;

    public CreateCommand(Database database) {
        this.database = database;
        name = "create";
        argumentsAmount = 2;
    }

    @Override
    protected boolean checkArguments(String[] args) throws Exception {
        return args.length >= 2;
    }

    @Override
    public void run(String[] args) throws Exception {
        String tableName = args[0];
        MyTable table = database.tableProvider.createTable(tableName, MyTable.convert(Arrays.copyOfRange(args, 1, args.length)));
        if (table == null) {
            System.out.println("tablename exists");
        } else {
            System.out.println("created");
        }
    }
}
