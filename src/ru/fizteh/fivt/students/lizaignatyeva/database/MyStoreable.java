package ru.fizteh.fivt.students.lizaignatyeva.database;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

public class MyStoreable implements Storeable {
    private final StoreableSignature storeableSignature;
    private ArrayList<Object> data;

    private static final HashSet<Class<?>> supportedClasses = new HashSet<>();

    static {
        supportedClasses.add(Integer.class);
        supportedClasses.add(Long.class);
        supportedClasses.add(Byte.class);
        supportedClasses.add(Float.class);
        supportedClasses.add(Double.class);
        supportedClasses.add(Boolean.class);
        supportedClasses.add(String.class);
    }

    public MyStoreable(StoreableSignature storeableSignature) {
        this.storeableSignature = storeableSignature;
        for (Class clazz : storeableSignature.columnClasses) {
            if (!supportedClasses.contains(clazz)) {
                throw new IllegalArgumentException("Unsupported class " + clazz.getCanonicalName());
            }
        }
        this.data = new ArrayList<>(this.storeableSignature.getColumnsCount());
        for (int i = 0; i < storeableSignature.getColumnsCount(); i++) {
            this.data.add(null);
        }
    }

    private void checkClass(Object object, int columnIndex) {
        if (object == null) {
            return;
        }
        Class<?> expectedClass = storeableSignature.getColumnClass(columnIndex);
        Class<?> providedClass = object.getClass();
        if (expectedClass != providedClass) {
            throw new ColumnFormatException("Expected " + expectedClass.getCanonicalName() + " class, " +
                    "but " + providedClass.getCanonicalName() + " got");
        }
    }

    private void checkClass(Class<?> clazz, int columnIndex) {
        Class<?> realClass = storeableSignature.getColumnClass(columnIndex);
        if (clazz != realClass) {
            throw new ColumnFormatException("Failed to convert " + realClass.getCanonicalName() +
                    " class to " + clazz.getClass() + " class ");
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        checkClass(value, columnIndex);
        data.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        return data.get(columnIndex);
    }


    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object object = getColumnAt(columnIndex);
        checkClass(Integer.class, columnIndex);
        return (Integer) object;
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object object = getColumnAt(columnIndex);
        checkClass(Long.class, columnIndex);
        return (Long) object;
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object object = getColumnAt(columnIndex);
        checkClass(Byte.class, columnIndex);
        return (Byte) object;
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object object = getColumnAt(columnIndex);
        checkClass(Float.class, columnIndex);
        return (Float) object;
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object object = getColumnAt(columnIndex);
        checkClass(Double.class, columnIndex);
        return (Double) object;
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object object = getColumnAt(columnIndex);
        checkClass(Boolean.class, columnIndex);
        return (Boolean) object;
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        Object object = getColumnAt(columnIndex);
        checkClass(String.class, columnIndex);
        return (String) object;
    }

    public String serialize() {
        JSONArray jsonArray = new JSONArray();
        for (Object value : data) {
            jsonArray.put(value);
        }
        return jsonArray.toString();
    }

    Object deserializeObject(JSONArray array, int columnIndex, Class<?> clazz) throws ParseException {
        if (array.isNull(columnIndex)) {
            return null;
        }
        try {
            if (clazz == Integer.class) {
                return array.getInt(columnIndex);
            } else if (clazz == Long.class) {
                return array.getLong(columnIndex);
            } else if (clazz == Byte.class) {
                return Byte.valueOf(Integer.valueOf(array.getInt(columnIndex)).toString());
            } else if (clazz == Float.class) {
                return Float.valueOf(Double.valueOf(array.getDouble(columnIndex)).toString());
            } else if (clazz == Double.class) {
                return array.getDouble(columnIndex);
            } else if (clazz == Boolean.class) {
                return array.getBoolean(columnIndex);
            } else if (clazz == String.class) {
                return array.getString(columnIndex);
            }
        } catch (Exception e) {
            throw new ParseException("Failed to parse JSON", 0);
        }
        throw new RuntimeException("Unsupported class found");
    }

    public void deserialize(String string) throws ParseException {
        try {
            JSONArray jsonArray = new JSONArray(string);
            if (jsonArray.length() != storeableSignature.getColumnsCount()) {
                throw new ParseException(String.format(
                        "Expected %d objects, but %d found",
                        storeableSignature.getColumnsCount(),
                        jsonArray.length()
                ), 0);
            }
            for (int index = 0; index < storeableSignature.getColumnsCount(); index++) {
                Object value = deserializeObject(jsonArray, index, storeableSignature.getColumnClass(index));
                checkClass(value, index);
                data.set(index, value);
            }
        } catch (JSONException e) {
            throw new ParseException("Failed to parse JSON", 0);
        } catch (ColumnFormatException e) {
            throw new ParseException("Failed to parse JSON: incorrect types", 0);
        }
    }

    public boolean equals(Object o) {
        MyStoreable storeable;
        try {
            storeable = (MyStoreable) o;
        } catch (Exception e) {
            return false;
        }
        if (o == null) {
            return false;
        }
        if (storeableSignature.getColumnsCount() != storeable.storeableSignature.getColumnsCount()) {
            return false;
        }
        for (int index = 0; index < storeableSignature.getColumnsCount(); index++) {
            if (data.get(index) == null) {
                return storeable.data.get(index) == null;
            }
            if (!data.get(index).equals(storeable.data.get(index))) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return storeableSignature.getColumnsCount();
    }
}
