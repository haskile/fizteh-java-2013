package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;

public class UseCommand extends Command {
    public UseCommand() {
        name = "use";
        argumentsAmount = 1;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (!checkArguments(args)) {
            throw new IllegalArgumentException("invalid usage");
        }
        //надо понять, писать ли таблицу в файл здесь
        //допилить сообщение об ошибке
        //не забыть, что вместо таблицы может быть null
        //а бывают ли роллбэки при новом запуске? нет, явно нет - таблица меняется только при сохраненных изменениях.
        //ок, давайте каждый раз писать таблицу
        Table table = DbMain.getCurrentTable();
        if (!table.saved()) {
            int diff = getChangesAmount();
            System.err.println(Integer.toString(diff) + " unsaved changes");
        }
        DbMain.saveCurrentTable();

        String tableName = args[0];
        if (!DbMain.tableExists(tableName)) {
            System.out.println(tableName + " not exists");
            return;
        }
        DbMain.setCurrentTable(tableName);
        System.out.println("using " + tableName);

    }
}
