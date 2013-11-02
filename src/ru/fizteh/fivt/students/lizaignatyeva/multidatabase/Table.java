package ru.fizteh.fivt.students.lizaignatyeva.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;


public class Table {
    final File path;
    final int BASE = 16;
    HashMap<String, String> data;
    String name;
    public Table(Path globalDirectory, String tableName) {
        path = globalDirectory.resolve(tableName).toFile();
        try {
            FileUtilities.mkDir(path);
        } catch (Exception e) {
            throw new Exception("Can't open table " + tableName);
        }
        try {
            data = new HashMap<String, String>();
            readTable();
        } catch (IOException e) {
            System.err.println("Error creating table: " + e.getMessage());
            System.exit(1);
        } catch (DataFormatException e) {
            System.err.println("Error creating table: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error creating table: " + e.getMessage());
            System.exit(1);
        }
    }

    public void delete() throws Exception {
        FileUtilities.remove(path);
    }

    private int getDirNumber(String key) {
        int byte = key.bytes[0];
        return byte % BASE;
    }

    private int getFileNumber(String key) {
        int byte = key.bytes[0];
        return (byte / BASE) % BASE;
    }

    private String getDirectoryName(String key) {
        return String.Format("%d.dir", getDirNumber(key));
    }

    private String getFileName(String key) {
        return String.Format("%d.dat", getFileNumber(key));
    }

    private boolean isValid(String key, int dirName, int fileName) {
        return !(getDirName(key).equals(dirName) && getFileName(key).equals(fileName));
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
        if (!isValid(key, dirName, fileName)) {
            throw new DataFormatException("entry in a wrong file, key: " + key);
        }
        String value = new String(valueBytes, StandardCharsets.UTF_8);
        if (dest.containsKey(key)) {
            throw new DataFormatException("duplicating keys: " + key);
        }
        data.put(key, value);
    }

    private boolean isValidDirectoryName(String name) {
        for (int i = 0; i < BASE; ++i) {
            if (name.equals(Integer.toString(i) + ".dir")) {
                return true;
            }
        }
        return false;
    }

    public void readTable() throws IOException, DataFormatException {
        for (File dir : path.listFiles) {
            if (!dir.isDirectory() || !isValidDirectoryName(dir.getName())) {
                throw new DataFormatException("Table '" + name + "' contains strange file(s)");
            }
            readData(dir);
        }
    }

    public void readData(File directory) throws IOException, DataFormatException {
        for (File file : directory.listFiles) {
            if (!isValidFileName(file.getName)) {
                throw new DataFormatException("Table '" + name + "' contains strange file(s)");
            }
            readFromFile(file.getCanonicalPath(), directory.getName(), file.getName());
        }

    }


    public void readFromFile(String filePath, String dirName, String fileName) throws IOException, DataFormatException {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        ByteBuffer buffer = ByteBuffer.wrap(data);
        try {
            while (buffer.hasRemaining()) {
                readEntry(buffer, dirName, fileName);
            }
        } catch (BufferUnderflowException e) {
            throw new DataFormatException("invalid file format");
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

    public void writeToFile() throws IOException {
        String fileName = path.getCanonicalPath();
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName));
        for (String key: data.keySet()) {
            String value = data.get(key);
            File directory = FileUtilities.mkDir(path.getAbsolutePath()
                                        + File.separator + getDirName(key));
            File file = FileUtilities.mkFile(directory.getAbsolutePath(), getFileName(key));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath()));
            try {
                writeEntry(key, value, outputStream);
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            } 
        }
    }

    public void write() {
        //it's a nice debugging tool i'd like to keep here
        System.out.println("we are off now");
        for (String str : data.keySet()) {
            System.out.println(str + " " + data.get(str));
        }

    }

}
