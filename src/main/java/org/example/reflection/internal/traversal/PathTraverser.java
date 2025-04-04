package org.example.reflection.internal.traversal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.example.reflection.api.ReflectionException;
import org.example.reflection.internal.operations.PropertyOperations;

/** Handles path traversal operations. */
public class PathTraverser {
    private final PropertyOperations propertyOperations = new PropertyOperations();

    /** Traverses a path in an object and returns the object at the specified path segment. */
    public Object traversePath(PropertyContext result) throws ReflectionException {
        if (result.getTarget() == null) {
            throw new ReflectionException("Null while traversing: " + result.getPropertyName());
        }

        Object next = handleMapOrNull(result);
        if (next != null) {
            return next;
        }

        try {
            return propertyOperations.getPropertyValue(result.getTarget(), result.getPropertyName());
        } catch (Exception e) {
            throw new ReflectionException("Error traversing path segment: " + result.getPropertyName(), e);
        }
    }

    /**
     * Traverses a path in an object and returns the object at the specified path segment, creating
     * intermediate objects if needed.
     */
    public Object traversePathAndCreateIfNeeded(PropertyContext result) throws ReflectionException {
        if (result.getTarget() == null) {
            throw new ReflectionException("Null while traversing: " + result.getPropertyName());
        }

        Object next = handleMapOrNull(result);
        if (next != null) {
            return next;
        }

        try {
            String getter = "get" + propertyOperations.capitalize(result.getPropertyName());
            try {
                Method method = result.getTarget().getClass().getMethod(getter);
                next = method.invoke(result.getTarget());
                if (next == null) {
                    next = propertyOperations.createAndSetIntermediateObject(
                            result.getTarget(), result.getPropertyName());
                }
                return next;
            } catch (NoSuchMethodException e) {
                Field field = result.getTarget().getClass().getDeclaredField(result.getPropertyName());
                field.setAccessible(true);
                next = field.get(result.getTarget());
                if (next == null) {
                    next = propertyOperations.createAndSetIntermediateObject(
                            result.getTarget(), result.getPropertyName());
                }
                return next;
            }
        } catch (Exception e) {
            throw new ReflectionException("Error traversing path segment: " + result.getPropertyName(), e);
        }
    }

    /** Handles null checks and map operations for path traversal. */
    private Object handleMapOrNull(PropertyContext result) throws ReflectionException {
        if (result.getTarget() instanceof Map<?, ?> map) {
            Object next = map.get(result.getPropertyName());
            if (next == null) {
                throw new ReflectionException("Map key missing at: " + result.getPropertyName());
            }
            return next;
        }

        return null;
    }
}
