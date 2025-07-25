package dev.lrxh.blockChanger.wrapper;

import dev.lrxh.blockChanger.utility.ReflectionUtility;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class CraftWrapper<T> {
    private final Object nms;

    public CraftWrapper(T input) {
        this.nms = apply(input);
    }

    public Class<?> cb(String className) {
        return ReflectionUtility.getClass("org.bukkit.craftbukkit." + className);
    }

    public Class<?> nms(String className) {
        return ReflectionUtility.getClass("net.minecraft." + className);
    }

    public Object nms() {
        return nms;
    }

    protected MethodHandle getMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... parameterTypes) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType methodType = MethodType.methodType(returnType, parameterTypes);

            try {
                return lookup.findVirtual(clazz, methodName, methodType);
            } catch (NoSuchMethodException e) {
                return lookup.findSpecial(clazz, methodName, methodType, clazz);
            }

        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get method handle for " + methodName + " in " + clazz.getName(), e);
        }
    }

    protected Method getReflectiveMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Failed to reflect method " + methodName + " in " + clazz.getName(), e);
        }
    }

    protected Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName + " in class " + clazz.getName(), e);
        }
    }

    public <T> T getFieldValue(Field field) {
        try {
            @SuppressWarnings("unchecked")
            T result = (T) field.get(null);
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get field value: " + field.getName(), e);
        }
    }

    protected abstract Object apply(T input);
}
