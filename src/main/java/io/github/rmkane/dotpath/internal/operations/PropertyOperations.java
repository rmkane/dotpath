package io.github.rmkane.dotpath.internal.operations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import io.github.rmkane.dotpath.api.DotPathException;

/**
 * Handles operations specific to object properties.
 */
public class PropertyOperations {
    /**
     * Gets a value from an object using a property name.
     */
    public Object getValueFromObject(Object obj, String propertyName) throws Exception {
        return getPropertyValue(obj, propertyName);
    }

    /**
     * Retrieves a property value from an object using reflection.
     */
    public Object getPropertyValue(Object obj, String propertyName)
            throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
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

        // Get the field type
        Field field = obj.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        // Check if the value's type is compatible with the field type
        if (!isCompatibleType(value.getClass(), fieldType)) {
            throw new DotPathException("Type mismatch: value type %s is not compatible with field type %s"
                    .formatted(value.getClass().getName(), fieldType.getName()));
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

        field.set(obj, value);
    }

    /**
     * Checks if two types are compatible for assignment.
     */
    private boolean isCompatibleType(Class<?> sourceType, Class<?> targetType) {
        // Handle primitive types and their wrapper classes
        if (sourceType.isPrimitive() || targetType.isPrimitive()) {
            return isPrimitiveCompatible(sourceType, targetType);
        }

        // Handle arrays
        if (sourceType.isArray() && targetType.isArray()) {
            return isCompatibleType(sourceType.getComponentType(), targetType.getComponentType());
        }

        // Handle regular classes
        return targetType.isAssignableFrom(sourceType);
    }

    /**
     * Checks if primitive types are compatible.
     */
    private boolean isPrimitiveCompatible(Class<?> sourceType, Class<?> targetType) {
        if (sourceType == targetType) {
            return true;
        }

        // Handle primitive and wrapper combinations
        if (sourceType == int.class
                && (targetType == Integer.class || targetType == long.class || targetType == Long.class)) {
            return true;
        }
        if (sourceType == Integer.class
                && (targetType == int.class || targetType == long.class || targetType == Long.class)) {
            return true;
        }
        if (sourceType == long.class && (targetType == Long.class)) {
            return true;
        }
        if (sourceType == Long.class && (targetType == long.class)) {
            return true;
        }
        if (sourceType == double.class && (targetType == Double.class)) {
            return true;
        }
        if (sourceType == Double.class && (targetType == double.class)) {
            return true;
        }
        if (sourceType == float.class && (targetType == Float.class)) {
            return true;
        }
        if (sourceType == Float.class && (targetType == float.class)) {
            return true;
        }
        if (sourceType == boolean.class && (targetType == Boolean.class)) {
            return true;
        }
        if (sourceType == Boolean.class && (targetType == boolean.class)) {
            return true;
        }

        return false;
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
