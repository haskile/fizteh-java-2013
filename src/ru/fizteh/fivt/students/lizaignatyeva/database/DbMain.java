package ru.fizteh.fivt.students.lizaignatyeva.database;


import ru.fizteh.fivt.students.lizaignatyeva.database.commands.*;
import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandFactory;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class DbMain {

    private static Hashtable<String, Command> createCommands(Database database) {
        Hashtable<String, Command> commandsMap = new Hashtable<String, Command>();
        commandsMap.put("put", new PutCommand(database));
        commandsMap.put("get", new GetCommand(database));
        commandsMap.put("remove", new RemoveCommand(database));
        commandsMap.put("exit", new ExitCommand(database));
        commandsMap.put("use", new UseCommand(database));
        commandsMap.put("create", new CreateCommand(database));
        commandsMap.put("drop", new DropCommand(database));
        commandsMap.put("commit", new CommitCommand(database));
        commandsMap.put("rollback", new RollbackCommand(database));
        return commandsMap;
    }

    public static void main(String[] args) {
        String dir = System.getProperty("fizteh.db.dir");
        MyTableProviderFactory providerFactory = new MyTableProviderFactory();
        MyTableProvider provider;

        try {
            provider = providerFactory.create(dir);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
            return;
        }

        Path directory = Paths.get(dir);
        CommandRunner runner = new CommandRunner(directory.toFile(), createCommands(new Database(provider)));
        runner.run(args);
    }
}
