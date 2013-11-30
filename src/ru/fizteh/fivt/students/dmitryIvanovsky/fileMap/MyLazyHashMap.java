package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class MyLazyHashMap extends MyHashMap {
    Path pathTable;
    int size;
    Set<String> keySet;
    final int numberDir = 16;
    final int numberFile = 16;
    Map<String, Storeable>[][] arrayMap = new HashMap[numberDir][numberFile];

    MyLazyHashMap(Path pathTable) {
        for (int i = 0; i < numberDir; ++i) {
            for (int j = 0; j < numberFile; ++j) {
                arrayMap[i][j] = new HashMap<>();
            }
        }
        size = 0;
        keySet = new HashSet<>();
        this.pathTable = pathTable;
    }

    public void loadTableFile(File randomFile, MyHashMap dbMap, String nameDir) throws Exception {
        if (randomFile.isDirectory()) {
            throw new ErrorFileMap("data file can't be a directory");
        }
        int intDir = FileMapUtils.getCode(nameDir);
        int intFile = FileMapUtils.getCode(randomFile.getName());

        RandomAccessFile dbFile = null;
        Exception error = null;
        try {
            try {
                dbFile = new RandomAccessFile(randomFile, "rw");
            } catch (Exception e) {
                throw new ErrorFileMap("file doesn't open");
            }
            if (dbFile.length() == 0) {
                throw new ErrorFileMap("file is clear");
            }
            dbFile.seek(0);

            byte[] arrayByte;
            Vector<Byte> vectorByte = new Vector<Byte>();
            long separator = -1;

            while (dbFile.getFilePointer() != dbFile.length()) {
                byte currentByte = dbFile.readByte();
                if (currentByte == '\0') {
                    int point1 = dbFile.readInt();
                    if (separator == -1) {
                        separator = point1;
                    }
                    long currentPoint = dbFile.getFilePointer();

                    while (dbFile.getFilePointer() != separator) {
                        if (dbFile.readByte() == '\0') {
                            break;
                        }
                    }

                    int point2;
                    if (dbFile.getFilePointer() == separator) {
                        point2 = (int) dbFile.length();
                    } else {
                        point2 = dbFile.readInt();
                    }

                    dbFile.seek(point1);

                    arrayByte = new byte[point2 - point1];
                    dbFile.readFully(arrayByte);
                    String value = new String(arrayByte, StandardCharsets.UTF_8);

                    arrayByte = new byte[vectorByte.size()];
                    for (int i = 0; i < vectorByte.size(); ++i) {
                        arrayByte[i] = vectorByte.elementAt(i).byteValue();
                    }
                    String key = new String(arrayByte, StandardCharsets.UTF_8);

                    if (dbMap.getHashDir(key) != intDir || dbMap.getHashFile(key) != intFile) {
                        throw new ErrorFileMap("wrong key in the file");
                    }

                    //TODO imprortant place
                    //dbMap.put(key, parent.deserialize(this, value));

                    vectorByte.clear();
                    dbFile.seek(currentPoint);
                } else {
                    vectorByte.add(currentByte);
                }
            }
        } catch (Exception e) {
            error = e;
            throw error;
        } finally {
            try {
                dbFile.close();
            } catch (Exception e) {
                if (error != null) {
                    error.addSuppressed(e);
                }
            }
        }
    }

    private Map<String, Storeable> initMap(String key) {
        Map<String, Storeable> map = arrayMap[getHashDir(key)][getHashFile(key)];
        return map;
    }

    public Storeable get(String key) {
        return initMap(key).get(key);
    }

    public void put(String key, Storeable value) {
        if (!keySet.contains(key)) {
            keySet.add(key);
            ++size;
        }
        initMap(key).put(key, value);
    }

    public void remove(String key) {
        if (keySet.contains(key)) {
            keySet.remove(key);
            --size;
        }
        initMap(key).remove(key);
    }

    public int getHashDir(String key) {
        int hashcode = key.hashCode();
        int ndirectory = hashcode % numberDir;
        if (ndirectory < 0) {
            ndirectory *= -1;
        }
        return ndirectory;
    }

    public int getHashFile(String key) {
        int hashcode = key.hashCode();
        int nfile = hashcode / numberDir % numberFile;
        if (nfile < 0) {
            nfile *= -1;
        }
        return nfile;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public Set<String> keySet() {
        return keySet;
    }

    public Boolean containsKey(String key) {
        return (keySet.contains(key));
    }

    public void clear() {
        for (String key : keySet) {
            initMap(key).remove(key);
        }
        keySet = new HashSet<>();
        size = 0;
    }

    public Map<String, Storeable> getMap(int i, int j) {
        return arrayMap[i][j];
    }

}