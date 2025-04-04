package org.example.reflection.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handles property access operations.
 */
public class PropertyAccessor {
    private final ValidationUtils validationUtils = new ValidationUtils();

    /**
     * Gets a value from an object using a property name.
     */
    public Object getValueFromObject(Object obj, String propertyName) throws Exception {
        return getPropertyValue(obj, propertyName);
    }

    /**
     * Retrieves a property value from an object using reflection.
     */
    public Object getPropertyValue(Object obj, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        String getter = "get" + capitalize(propertyName);
        try {
            Method method = obj.getClass().getMethod(getter);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            Field field = obj.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(obj);
        }
    }

    /**
     * Sets a value on an object using a property name.
     */
    public <T> void setValueOnObject(Object obj, String propertyName, T value) throws Exception {
        if (obj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            map.put(propertyName, value);
            return;
        }

        String setter = "set" + capitalize(propertyName);
        for (Method m : obj.getClass().getMethods()) {
            if (m.getName().equals(setter)
                && m.getParameterCount() == 1
                && m.getParameterTypes()[0].isAssignableFrom(value.getClass())) {
                m.invoke(obj, value);
                return;
            }
        }

        Field field = obj.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /**
     * Creates and sets an intermediate object for a path segment.
     */
    public Object createAndSetIntermediateObject(Object current, String part) throws Exception {
        Field field = current.getClass().getDeclaredField(part);
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        Object instance = fieldType.getDeclaredConstructor().newInstance();
        field.set(current, instance);
        return instance;
    }

    /**
     * Capitalizes the first character of a string.
     */
    public String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
} 