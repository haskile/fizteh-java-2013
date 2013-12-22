package ru.fizteh.fivt.students.lizaignatyeva.database.httpserver;

import ru.fizteh.fivt.students.lizaignatyeva.database.MyTable;
import ru.fizteh.fivt.students.lizaignatyeva.database.MyTableProvider;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    public MyTableProvider tableProvider;
    public HashMap<String, MyTable> transactions;
    private final ReentrantReadWriteLock transactionLock;

    private static final int MAX_TRANSACTION_ID = 1000000;

    public Database(MyTableProvider tableProvider) {
        this.tableProvider = tableProvider;
        this.transactions = new HashMap<>();
        this.transactionLock = new ReentrantReadWriteLock();
    }

    public MyTable getTransaction(String transactionName) {
        transactionLock.readLock().lock();
        try {
            return transactions.get(transactionName);
        } finally {
            transactionLock.readLock().unlock();
        }
    }

    public void cancelTransaction(String transactionName) {
        transactionLock.writeLock().lock();
        try {
            transactions.remove(transactionName);
        } finally {
            transactionLock.writeLock().unlock();
        }
    }

    public String generateTransactionName(String tableName) {
        transactionLock.writeLock().lock();
        try {
            for (int transactionId = 0; transactionId < MAX_TRANSACTION_ID; transactionId ++) {
                String transactionName = String.format("%05d", transactionId);
                if (!transactions.containsKey(transactionName)) {
                    MyTable table = tableProvider.getTable(tableName);
                    transactions.put(transactionName, table);
                    return transactionName;
                }
            }
            return null;
        } finally {
            transactionLock.writeLock().unlock();
        }
    }
}
