package ru.fizteh.fivt.students.zhidkovanton.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;

public class DataFactory implements TableProvider {
    private String currentTable = null;
    private String tableDir;

    public DataFactory(String directory) {
        tableDir = directory;
    }

    public static void deleteDirectory(String dir) {
        File file = new File(dir);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isFile()) {
                if (!files[i].delete()) {
                    throw new FileAccessException("Cant delete file " + file.toString());
                }
            } else {
                deleteDirectory(files[i].toString());
            }
        }
        if (!file.delete()) {
            throw new FileAccessException("Cant delete directory " + file.toString());
        }
    }

    @Override
    public void removeTable(String name) {
        checkName(name);
        if (isExists(name)) {
            String fullName = tableDir + File.separator + name;
            deleteDirectory(fullName);
        } else {
            throw new IllegalStateException("Table doesn't exist");
        }

    }

    public String getCurrentName() {
        return currentTable;
    }

    private void checkName(String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table! Wrong name!");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+") || name.contains(File.separator)
                || name.contains(".")) {
            throw new IllegalArgumentException("Wrong symbols in name!");
        }
    }

    @Override
    public DataBase createTable(String name) {
        checkName(name);
        String fullName = tableDir + File.separator + name;

        File file = new File(fullName);

        if (file.exists()) {
            return null;
        }

        if (name == null) {
            System.exit(1);
        }
        if (!file.mkdir()) {
            throw new IllegalArgumentException("Cannot create table " + tableDir);
        }

        return new DataBase();
    }

    @Override
    public DataBase getTable(String name) {
        checkName(name);
        String fullName = tableDir + File.separator + name;

        File file = new File(fullName);

        if (!file.exists()) {
            return null;
        }
        currentTable = name;

        return new DataBase(name);
    }

    public boolean isExists(String name) {
        checkName(name);
        String fullName = tableDir + File.separator + name;

        File file = new File(fullName);

        if (file.exists()) {
            return true;
        }
        return false;
    }
}
