package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class PutCommand extends Command {
    public PutCommand() {
        name = "put";
        argumentsAmount = 2;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }


        Table table = DbMain.getCurrentTable();
        if (table == null) {
            System.out.println("no table");
            return;
        }
        //System.err.println("HERE");
        String key = args[0];
        String value = args[1];



        if (table.containsKey(key)) {
            if (!table.get(key).equals(value)) {
                table.wasChanged();
            }
            System.out.println("overwrite");
            System.out.println(table.get(key));
            table.remove(key);
            table.put(key, value);
        } else {
            table.wasChanged();
            System.out.println("new");
            table.put(key, value);
        }

    }
}
