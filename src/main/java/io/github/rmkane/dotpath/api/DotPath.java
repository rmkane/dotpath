package io.github.rmkane.dotpath.api;

import io.github.rmkane.dotpath.internal.TypeResolver;
import io.github.rmkane.dotpath.internal.ValidationUtils;
import io.github.rmkane.dotpath.internal.operations.MapOperations;
import io.github.rmkane.dotpath.internal.operations.PropertyOperations;
import io.github.rmkane.dotpath.internal.traversal.PathTraverser;
import io.github.rmkane.dotpath.internal.traversal.PropertyContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Main utility class for reflection operations. Provides high-level methods for getting and setting
 * values using dot-notation paths.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DotPath {
    private static final PathTraverser pathTraverser = new PathTraverser();
    private static final PropertyOperations propertyOperations = new PropertyOperations();
    private static final TypeResolver typeResolver = new TypeResolver();
    private static final ValidationUtils validationUtils = new ValidationUtils();
    private static final MapOperations mapOperations = new MapOperations();

    /**
     * Traverses a path in an object and returns the final object and property name.
     *
     * @param root The root object to traverse
     * @param path The dot-notation path to traverse
     * @return A context containing the final object and property name
     * @throws DotPathException if the path is invalid or inaccessible
     */
    private static PropertyContext traversePath(Object root, String path) throws DotPathException {
        validationUtils.validateInput(root, path);

        String[] parts = path.split("\\.");
        Object current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validationUtils.validatePathSegment(part);

            current = pathTraverser.traversePathAndCreateIfNeeded(new PropertyContext(current, part));
        }

        String finalPart = parts[parts.length - 1];
        validationUtils.validatePathSegment(finalPart);

        return new PropertyContext(current, finalPart);
    }

    /**
     * Gets a value from an object using a dot-notation path.
     *
     * @param root The root object to traverse
     * @param path The dot-notation path to the desired property
     * @return The value at the specified path
     * @throws DotPathException if the path is invalid or inaccessible
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object root, String path) throws DotPathException {
        PropertyContext context = traversePath(root, path);
        Object target = context.getTarget();
        String propertyName = context.getPropertyName();

        if (mapOperations.isMap(target)) {
            return mapOperations.getValue(mapOperations.asMap(target), propertyName);
        }

        try {
            return (T) propertyOperations.getPropertyValue(target, propertyName);
        } catch (Exception e) {
            throw new DotPathException("Error getting value at path: " + path, e);
        }
    }

    /**
     * Sets a value in an object using a dot-notation path.
     *
     * @param root  The root object to traverse
     * @param path  The dot-notation path to the desired property
     * @param value The value to set
     * @throws DotPathException if the path is invalid or inaccessible
     */
    public static <T> void set(Object root, String path, T value) throws DotPathException {
        PropertyContext context = traversePath(root, path);
        Object target = context.getTarget();
        String propertyName = context.getPropertyName();

        try {
            if (mapOperations.isMap(target)) {
                mapOperations.setValue(mapOperations.asMap(target), propertyName, value);
            } else {
                propertyOperations.setValueOnObject(target, propertyName, value);
            }
        } catch (Exception e) {
            throw new DotPathException("Error setting value at path: " + path, e);
        }
    }

    /**
     * Copies a property value from source to target object.
     *
     * @param source Source object to copy from
     * @param target Target object to copy to
     * @param path   Property path to copy
     * @throws DotPathException if types are incompatible or property not found
     */
    public static void copy(Object source, Object target, String path) throws DotPathException {
        if (source == null || target == null) {
            throw new DotPathException("Source and target objects cannot be null");
        }

        // First check if source and target are of compatible types
        if (!source.getClass().equals(target.getClass())) {
            throw new DotPathException("Source type %s and target type %s are incompatible"
                    .formatted(source.getClass().getName(), target.getClass().getName()));
        }

        try {
            Class<?> sourceType = typeResolver.resolveType(source, path);
            Class<?> targetType = typeResolver.resolveType(target, path);

            if (!typeResolver.isCompatibleType(sourceType, targetType)) {
                throw new DotPathException("Type mismatch: cannot copy from %s to %s"
                        .formatted(sourceType.getName(), targetType.getName()));
            }

            Object value = get(source, path);
            set(target, path, value);
        } catch (Exception e) {
            throw new DotPathException("Failed to copy property: " + e.getMessage(), e);
        }
    }

    /**
     * Sets a value in an object using a dot-notation path, converting the string value to the
     * appropriate type.
     *
     * @param root     The root object to traverse
     * @param path     The dot-notation path to the desired property
     * @param valueStr The string value to convert and set
     * @throws DotPathException if the path is invalid or inaccessible
     */
    public static void setFromString(Object root, String path, String valueStr) throws DotPathException {
        validationUtils.validateInput(root, "root");
        validationUtils.validateInput(path, "path");
        validationUtils.validateInput(valueStr, "valueStr");

        try {
            Class<?> targetType = typeResolver.resolveType(root, path);
            Object value = typeResolver.parseValueByType(targetType, valueStr);
            set(root, path, value);
        } catch (Exception e) {
            throw new DotPathException("Error setting value from string at path: " + path, e);
        }
    }
}
