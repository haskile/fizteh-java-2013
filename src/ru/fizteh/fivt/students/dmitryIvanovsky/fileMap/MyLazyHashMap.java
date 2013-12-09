package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class MyLazyHashMap {
    Path pathTable;
    int size;
    final int numberDir = 16;
    final int numberFile = 16;
    boolean[][] loadFile = new boolean[16][16];
    Map<String, Storeable>[][] arrayMap = new WeakHashMap[numberDir][numberFile];
    FileMapProvider provider;
    FileMap fileMap;

    MyLazyHashMap(Path pathTable, FileMapProvider provider, FileMap fileMap) {
        for (int i = 0; i < numberDir; ++i) {
            for (int j = 0; j < numberFile; ++j) {
                arrayMap[i][j] = new WeakHashMap<>();
                loadFile[i][j] = false;
            }
        }
        size = 0;
        this.pathTable = pathTable;
        this.provider = provider;
        this.fileMap = fileMap;
    }

    private boolean isFileLoad(String key) {
        return loadFile[getHashDir(key)][getHashFile(key)];
    }

    private void loadFile(String key) throws Exception {
        int dir = getHashDir(key);
        int file = getHashFile(key);
        if (!arrayMap[dir][file].containsKey(key)) {
            loadTableFile(dir, file, key);
            loadFile[dir][file] = true;
        }
    }

    public void loadTableFile(int intDir, int intFile, String refKey) throws Exception {
        String strDir = String.valueOf(intDir) + ".dir";
        File randomFile = pathTable.resolve(strDir).resolve(String.valueOf(intFile) + ".dat").toFile();

        if (!randomFile.exists()) {
            throw new FileIsNotExist();
        }

        if (randomFile.isDirectory()) {
            throw new ErrorFileMap("data file can't be a directory");
        }

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

                    if (getHashDir(key) != intDir || getHashFile(key) != intFile) {
                        throw new ErrorFileMap("wrong key in the file");
                    }

                    if (key.equals(refKey)) {
                        arrayMap[intDir][intFile].put(key, provider.deserialize(fileMap, value));
                        break;
                    }

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
        return arrayMap[getHashDir(key)][getHashFile(key)];
    }

    public Storeable get(String key) {
        try {
            loadFile(key);
            return initMap(key).get(key);
        } catch (FileIsNotExist e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Error with open file with key = " + key, e);
        }
    }

    public void put(String key, Storeable value) {
        try {
            loadFile(key);
        } catch (FileIsNotExist e) {
            //pass
        } catch (Exception e) {
            throw new IllegalStateException("Error with open file with key = " + key, e);
        }
        initMap(key).put(key, value);
    }

    public void remove(String key) {
        try {
            loadFile(key);
            initMap(key).remove(key);
        } catch (FileIsNotExist e) {
            //pass
        } catch (Exception e) {
            throw new IllegalStateException("Error with open file with key = " + key, e);
        }
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

    public Boolean containsKey(String key) {
        try {
            loadFile(key);
            return initMap(key).containsKey(key);
        } catch (FileIsNotExist e) {
            return false;
        } catch (Exception e) {
            throw new IllegalStateException("Error with open file with key = " + key, e);
        }
    }

    public Map<String, Storeable> getMap(int i, int j) {
        return arrayMap[i][j];
    }

}

class FileIsNotExist extends Exception {
    public FileIsNotExist() {
        super();
    }
}
