package io.github.rmkane.dotpath.internal.traversal;

import java.util.HashMap;

import io.github.rmkane.dotpath.api.DotPathException;
import io.github.rmkane.dotpath.internal.operations.MapOperations;
import io.github.rmkane.dotpath.internal.operations.PropertyOperations;

/**
 * Handles path traversal operations with consistent behavior for maps and objects.
 */
public class PathTraverser {
    private final PropertyOperations propertyOperations = new PropertyOperations();
    private final MapOperations mapOperations = new MapOperations();

    /**
     * Traverses a path in an object and returns the object at the specified path segment.
     *
     * @param context The property context containing target object and property name
     * @return The value at the specified path
     * @throws DotPathException if the path is invalid or inaccessible
     */
    public Object traversePath(PropertyContext context) throws DotPathException {
        validateContext(context);

        if (mapOperations.isMap(context.getTarget())) {
            return mapOperations.getValue(mapOperations.asMap(context.getTarget()), context.getPropertyName());
        }

        return getPropertyValue(context);
    }

    /**
     * Traverses a path in an object and returns the object at the specified path segment,
     * creating intermediate objects if needed.
     *
     * @param context The property context containing target object and property name
     * @return The value at the specified path, creating new objects as needed
     * @throws DotPathException if the path is invalid or inaccessible
     */
    public Object traversePathAndCreateIfNeeded(PropertyContext context) throws DotPathException {
        validateContext(context);

        if (mapOperations.isMap(context.getTarget())) {
            return mapOperations
                    .asMap(context.getTarget())
                    .computeIfAbsent(context.getPropertyName(), k -> new HashMap<>());
        }

        return getOrCreatePropertyValue(context);
    }

    private void validateContext(PropertyContext context) throws DotPathException {
        if (context.getTarget() == null) {
            throw new DotPathException("Null while traversing: " + context.getPropertyName());
        }
    }

    private Object getPropertyValue(PropertyContext context) throws DotPathException {
        try {
            return propertyOperations.getPropertyValue(context.getTarget(), context.getPropertyName());
        } catch (Exception e) {
            throw new DotPathException("Error traversing path segment: " + context.getPropertyName(), e);
        }
    }

    private Object getOrCreatePropertyValue(PropertyContext context) throws DotPathException {
        try {
            Object value = propertyOperations.getPropertyValue(context.getTarget(), context.getPropertyName());
            if (value != null) {
                return value;
            }
            return propertyOperations.createAndSetIntermediateObject(context.getTarget(), context.getPropertyName());
        } catch (Exception e) {
            throw new DotPathException("Error traversing path segment: " + context.getPropertyName(), e);
        }
    }
}
