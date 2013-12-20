package ru.fizteh.fivt.students.lizaignatyeva.database;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.storage.strings.Table;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.DataFormatException;


public class MyTable implements Table {
    private Path globalDirectory;
    private String name;
    private HashMap<String, String> data;
    private HashMap<String, String> uncommitedData;
    private int currentSize;
    private int currentChanges;

    private final int base = 16;


    public MyTable(Path globalDirectory, String name) {
        this.globalDirectory = globalDirectory;
        this.name = name;
        this.currentSize = 0;
        this.currentChanges = 0;
        this.data = new HashMap<>();
        this.uncommitedData = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Table.get: null key provided");
        }
        if (uncommitedData.containsKey(key)) {
            return uncommitedData.get(key);
        }
        if (data.containsKey(key)) {
            return data.get(key);
        }
        return null;
    }

    @Override
    public String put(String key, String value) {
        if (key == null || key.equals("")) {
            throw new IllegalArgumentException("Table.put: null key provided");
        }
        if (value == null) {
            throw new IllegalArgumentException("Table.put: null value provided");
        }
        currentChanges ++;
        String result = null;
        if (uncommitedData.containsKey(key)) {
            result = uncommitedData.get(key);
        } else if (data.containsKey(key)) {
            result = data.get(key);
        }
        if (result == null) {
            currentSize++;
        }
        uncommitedData.put(key, value);
        return result;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Table.remove: null key provided");
        }
        String result = null;
        if (uncommitedData.containsKey(key)) {
            result = uncommitedData.get(key);
            uncommitedData.put(key, null);
        } else {
            if (data.containsKey(key)) {
                result = data.get(key);
                uncommitedData.put(key, null);
                currentChanges ++;
            }
        }
        if (result != null) {
            currentSize--;
        }
        return result;
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public int commit() {
        int result = keysToCommit();
        for (String key : uncommitedData.keySet()) {
            String value = uncommitedData.get(key);
            if (value == null) {
                if (data.containsKey(key)) {
                    data.remove(key);
                }
            } else {
                data.put(key, value);
            }
        }
        try {
            write();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write changes in table '" + name + "' to disk");
        }
        currentChanges = 0;
        uncommitedData = new HashMap<>();
        return result;
    }

    @Override
    public int rollback() {
        int result = currentChanges;
        uncommitedData = new HashMap<>();
        currentSize = data.size();
        currentChanges = 0;
        return result;
    }

    private void recalcSize() {
        HashSet<String> keys = new HashSet<>();
        for (String key : data.keySet()) {
            keys.add(key);
        }
        for (String key : uncommitedData.keySet()) {
            String value = uncommitedData.get(key);
            if (value == null) {
                keys.remove(key);
            } else {
                keys.add(key);
            }
        }
        currentSize = keys.size();
    }

    public boolean exists() {
        try {
            File path = globalDirectory.resolve(name).toFile();
            return path.isDirectory();
        } catch (Exception e) {
        }
        return false;
    }

    public void read() throws DataFormatException {
        data = new HashMap<>();
        uncommitedData = new HashMap<>();
        currentChanges = 0;

        File path = globalDirectory.resolve(name).toFile();
        File[] subDirs = path.listFiles();
        if (subDirs == null) {
            return;
        }
        for (File dir: subDirs) {
            if (!dir.isDirectory() || !isValidDirectoryName(dir.getName())) {
                throw new DataFormatException("Table '" + name + "' contains strange file: '" + dir.getName() + "'");
            }
            readDirectory(dir);
        }
        recalcSize();
    }

    private void readDirectory(File dir) throws DataFormatException {
        File[] filesInDirectory = dir.listFiles();
        if (filesInDirectory == null) {
            return;
        }
        for (File file : filesInDirectory) {
            if (!file.isFile() || !isValidFileName(file.getName())) {
                throw new DataFormatException("Table '" + name + "' contains strange file: '" + file.getName() + "'");
            }
            try {
                readFile(file.getCanonicalPath(), dir.getName(), file.getName());
            } catch (IOException e) {
                throw new DataFormatException("Failed to fetch files for table: " + name);
            }
        }
    }

    public void readFile(String filePath, String dirName, String fileName) throws DataFormatException, IOException {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        ByteBuffer buffer = ByteBuffer.wrap(data);
        while (buffer.hasRemaining()) {
            try {
                readEntry(buffer, dirName, fileName);
            } catch (BufferUnderflowException e) {
                throw new DataFormatException("Table '" + name + "' contains corrupted file " + filePath);
            }
        }
    }

    private void readEntry(ByteBuffer buffer, String dirName, String fileName) throws BufferUnderflowException,
            DataFormatException {
        int keyLength = buffer.getInt();
        if (keyLength > buffer.remaining() || keyLength < 0) {
            throw new DataFormatException("too long key buffer");
        }
        int valueLength = buffer.getInt();
        if (valueLength > buffer.remaining() || valueLength < 0) {
            throw new DataFormatException("too long value buffer");
        }
        byte[] keyBytes = new byte[keyLength];
        buffer.get(keyBytes);
        byte[] valueBytes = new byte[valueLength];
        buffer.get(valueBytes);
        String key = new String(keyBytes, StandardCharsets.UTF_8);
        if (!isValidKey(key, dirName, fileName)) {
            throw new DataFormatException("entry in a wrong file, key: " + key + ", file: "
                    + fileName + ", expected file: " + getFileName(key) + ", directory: " + dirName
                    + ", expected directory: " + getDirName(key));
        }
        String value = new String(valueBytes, StandardCharsets.UTF_8);
        if (data.containsKey(key)) {
            throw new DataFormatException("duplicating keys: " + key);
        }
        data.put(key, value);
    }

    private boolean isValidKey(String key, String dirName, String fileName) {
        return getDirName(key).equals(dirName) && getFileName(key).equals(fileName);
    }

    private boolean isValidDirectoryName(String name) {
        for (int i = 0; i < base; ++i) {
            if (name.equals(Integer.toString(i) + ".dir")) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidFileName(String name) {
        for (int i = 0; i < base; ++i) {
            if (name.equals(Integer.toString(i) + ".dat")) {
                return true;
            }
        }
        return false;
    }

    public void write() throws IOException {
        File path = globalDirectory.resolve(name).toFile();
        try {
            FileUtils.remove(path);
        } catch (Exception e) {
            //System.err.println("Error while updating database files: " + e.getMessage());
            //System.exit(1);
        }
        FileUtils.mkDir(path.getAbsolutePath());
        for (String key: data.keySet()) {
            String value = data.get(key);
            File directory = FileUtils.mkDir(path.getAbsolutePath()
                    + File.separator + getDirName(key));
            File file = FileUtils.mkFile(directory, getFileName(key));
            try (BufferedOutputStream outputStream = new BufferedOutputStream(
                    new FileOutputStream(file.getCanonicalPath(), true))) {
                writeEntry(key, value, outputStream);
            }
        }
    }

    private byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    private void writeEntry(String key, String value, BufferedOutputStream outputStream) throws IOException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        outputStream.write(intToBytes(keyBytes.length));
        outputStream.write(intToBytes(valueBytes.length));
        outputStream.write(keyBytes);
        outputStream.write(valueBytes);
    }

    public int keysToCommit() {
        return uncommitedData.size();
    }

    private int getDirNumber(String key) {
        int number = key.getBytes()[0];
        number = Math.abs(number);
        return number % base;
    }

    private int getFileNumber(String key) {
        int number = key.getBytes()[0];
        number = Math.abs(number);
        return number / base % base;
    }

    private String getDirName(String key) {
        return String.format("%d.dir", getDirNumber(key));
    }

    private String getFileName(String key) {
        return String.format("%d.dat", getFileNumber(key));
    }

}
