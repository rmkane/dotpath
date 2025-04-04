package org.example.reflection.internal;

import org.example.reflection.ReflectionException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handles path traversal operations.
 */
public class PathTraverser {
    private final PropertyAccessor propertyAccessor = new PropertyAccessor();

    /**
     * Traverses a path in an object and returns the object at the specified path segment.
     */
    public Object traversePath(Object current, String part) throws ReflectionException {
        Object result = handleMapOrNull(current, part);
        if (result != null) {
            return result;
        }

        try {
            return propertyAccessor.getPropertyValue(current, part);
        } catch (Exception e) {
            throw new ReflectionException("Error traversing path segment: " + part, e);
        }
    }

    /**
     * Traverses a path in an object and returns the object at the specified path segment,
     * creating intermediate objects if needed.
     */
    public Object traversePathAndCreateIfNeeded(Object current, String part) throws ReflectionException {
        Object result = handleMapOrNull(current, part);
        if (result != null) {
            return result;
        }

        try {
            String getter = "get" + propertyAccessor.capitalize(part);
            try {
                Method method = current.getClass().getMethod(getter);
                Object next = method.invoke(current);
                if (next == null) {
                    next = propertyAccessor.createAndSetIntermediateObject(current, part);
                }
                return next;
            } catch (NoSuchMethodException e) {
                Field field = current.getClass().getDeclaredField(part);
                field.setAccessible(true);
                Object next = field.get(current);
                if (next == null) {
                    next = propertyAccessor.createAndSetIntermediateObject(current, part);
                }
                return next;
            }
        } catch (Exception e) {
            throw new ReflectionException("Error traversing path segment: " + part, e);
        }
    }

    /**
     * Handles null checks and map operations for path traversal.
     */
    private Object handleMapOrNull(Object current, String part) throws ReflectionException {
        if (current == null) {
            throw new ReflectionException("Null while traversing: " + part);
        }

        if (current instanceof Map<?, ?> map) {
            Object next = map.get(part);
            if (next == null) {
                throw new ReflectionException("Map key missing at: " + part);
            }
            return next;
        }

        return null;
    }
} 