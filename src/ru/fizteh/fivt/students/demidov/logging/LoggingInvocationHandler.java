package ru.fizteh.fivt.students.demidov.logging;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

import org.json.JSONObject;

public class LoggingInvocationHandler implements InvocationHandler {
    private Writer writer;
    private Object object;

    public LoggingInvocationHandler(Object object, Writer writer) {
        this.writer = writer;
        this.object = object;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                result = method.invoke(object, args);
            } catch (InvocationTargetException catchedException) {
                throw catchedException.getTargetException();
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                result = method.invoke(object, args);
                if (!(method.getReturnType().equals(void.class))) {
                    LoggingUtils.logReturnValue(jsonObject, result);
                }
            } catch (InvocationTargetException catchedException) {
                jsonObject.put("thrown", catchedException.getTargetException().toString());
                writer.write(jsonObject.toString(2) + '\n');
                throw catchedException.getTargetException();
            }
            writer.write(jsonObject.toString(2) + '\n');
        }
            
        return result;
    }
}
