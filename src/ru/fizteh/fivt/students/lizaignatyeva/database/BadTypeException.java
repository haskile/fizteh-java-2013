package ru.fizteh.fivt.students.lizaignatyeva.database;

public class BadTypeException extends RuntimeException {
    public BadTypeException() {
    }

    public BadTypeException(String message) {
        super(message);
    }

    public BadTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadTypeException(Throwable cause) {
        super(cause);
    }

    public BadTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
