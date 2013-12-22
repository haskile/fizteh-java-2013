package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TransactionPool {
    private HashMap<String, HashMap<String, Storeable>> transactions;
    private HashMap<String, MultiFileMap> tables;
    private Queue<Integer> available;
    private int idLength;

    public TransactionPool(int idLength) {
        this.idLength = idLength;
        transactions = new HashMap<>();
        tables = new HashMap<>();
        available = new LinkedList<>();
        int maxValue = 1;
        for (int i = 0; i < idLength; i++) {
            maxValue *= 10;
        }
        for (int i = 0; i < maxValue; i++) {
            available.add(i);
        }
    }

    private void checkTransactionId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Null id");
        }
        if (id.length() != idLength) {
            throw new IllegalArgumentException("Wrong id format");
        }
        for (int i = 0; i < id.length(); i++) {
            if (id.charAt(i) < '0' || id.charAt(i) > '9') {
                throw new IllegalArgumentException(
                        String.format("Illegal character $c should be a digit", id.charAt(i)));
            }
        }
    }

    String createTransaction(MultiFileMap table) {
        if (available.isEmpty()) {
            throw new IllegalStateException("No space for a new transaction");
        }
        int next = available.remove();
        String id = String.format("%05d", next);
        transactions.put(id, new HashMap<String, Storeable>());
        tables.put(id, table);
        return id;
    }

    void removeTransaction(String id) {
        checkTransactionId(id);
        if (transactions.remove(id) == null) {
            throw new IllegalArgumentException("Transaction doesn't exist");
        }
        tables.remove(id);
        available.add(Integer.parseInt(id));
    }

    HashMap<String, Storeable> getTransaction(String id) {
        checkTransactionId(id);
        HashMap<String, Storeable> result = transactions.get(id);
        if (result == null) {
            throw new IllegalArgumentException("Transaction doesn't exist");
        }
        return result;
    }

    MultiFileMap getTable(String id) {
        checkTransactionId(id);
        MultiFileMap result = tables.get(id);
        if (result == null) {
            throw new IllegalArgumentException("Transaction doesn't exist");
        }
        return result;
    }

    TransactionHandler createHandler(MultiFileMap table) {
        return new TransactionHandler(table, this);
    }
}
