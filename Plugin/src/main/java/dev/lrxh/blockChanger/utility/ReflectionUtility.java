package dev.lrxh.blockChanger.utility;

import java.util.HashMap;
import java.util.Map;

public class ReflectionUtility {
    private static final Map<String, Class<?>> cache = new HashMap<>();

    public static Class<?> getClass(String className) {
        try {
            if (cache.containsKey(className)) return cache.get(className);
            Class<?> clazz = Class.forName(className);
            cache.put(className, clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className, e);
        }
    }
}
