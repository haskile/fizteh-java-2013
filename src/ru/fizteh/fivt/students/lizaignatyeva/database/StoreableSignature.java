package ru.fizteh.fivt.students.lizaignatyeva.database;

import java.util.ArrayList;

public class StoreableSignature {
    public final ArrayList<Class<?>> columnClasses;

    public StoreableSignature(ArrayList<Class<?>> columnClasses) {
        this.columnClasses = columnClasses;
    }

    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses.get(columnIndex);
    }

    public int getColumnsCount() {
        return columnClasses.size();
    }
}
