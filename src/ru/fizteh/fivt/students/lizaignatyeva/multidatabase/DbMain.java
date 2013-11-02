package ru.fizteh.fivt.students.lizaignatyeva.database;


import ru.fizteh.fivt.students.lizaignatyeva.shell.Command;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandFactory;
import ru.fizteh.fivt.students.lizaignatyeva.shell.CommandRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class DbMain {
    static Path directory;
    static CommandFactory factory;

    private static Hashtable<String, Table> tables;
    private static Hashtable<String, Command> commandsMap;

    static Table currentTable;

    public static Table getCurrentTable() throws Exception {
        //return databases.get(currentDatabaseName); MULTI ONLY
        return currentTable;
    }

    public static Table getTable(name) throws Exception {
        return tables[name];
    }

    public static boolean tableExists(name) throws Exception {
        return tables.containsKey(name);
    }

    public static void setCurrentTable(String name) throws Exception {
        currentTable = tables[name];
    }

    public static void addTable(Table table) throws Exception {
        tables.put(table.name, table);
    }

    public static String concatenateWithDelimiter(String[] strings, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            builder.append(strings[i]);
            if (i != strings.length - 1) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    private static void addCommands() {
        commandsMap = new Hashtable<String, Command>();
        commandsMap.put("put", new PutCommand());
        commandsMap.put("get", new GetCommand());
        commandsMap.put("remove", new RemoveCommand());
        commandsMap.put("exit", new ExitCommand());
        commandsMap.put("use", new UseCommand());
        commandsMap.put("create", new CreateCommand());
        commandsMap.put("drop", new DropCommand());
    }

    public static void main(String[] args) {
        //TODO: remove that for MULTI
        try {
            String dir = System.getProperty("fizteh.db.dir");
            if (dir == null) {
                throw new Exception("directory not declared");
            }
            directory = Paths.get(dir);
            if (!Files.isDirectory(directory)) {
                throw new Exception(directory + " doesn't exist or is not a directory");
            }
        } catch (Exception e) {
            System.err.println("Error opening database" + (e.getMessage() == null ? "" : (": " + e.getMessage())));
            System.exit(1);
        }
/*
        path = path.resolve("db.dat");

        currentTable = new Database(path.toString()); // SIMPLE ONLY*/
        addCommands();
        CommandRunner runner = new CommandRunner(path.toFile(), commandsMap);
        runner.run(args);

    }
}
