package nota.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ClassInfo {

    public final Class<?> clazz;
    public final HashMap<String, Method> methods;
    public final HashMap<String, Field> fields;
    public final String className;

    public ClassInfo(Class<?> clazz, String className) {
        methods = new HashMap<String, Method>();
        fields = new HashMap<String, Field>();
        this.clazz = clazz;
        this.className = className;
    }

    public void addCachedMethod(String key, Method method) {
        methods.put(key, method);
    }

    public Method getCachedMethod(String key) {
        return methods.get(key);
    }

    public void addCachedField(String key, Field field) {
        fields.put(key, field);
    }

    public Field getCachedField(String key) {
        return fields.get(key);
    }
}
