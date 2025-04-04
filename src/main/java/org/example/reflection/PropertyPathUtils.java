package org.example.reflection;

import java.util.Map;

import org.example.reflection.internal.PathTraverser;
import org.example.reflection.internal.PropertyAccessor;
import org.example.reflection.internal.TypeResolver;
import org.example.reflection.internal.ValidationUtils;

/**
 * Main utility class for reflection operations. Provides high-level methods for getting and setting
 * values using dot-notation paths.
 */
public class PropertyPathUtils {
    private static final PathTraverser pathTraverser = new PathTraverser();
    private static final PropertyAccessor propertyAccessor = new PropertyAccessor();
    private static final TypeResolver typeResolver = new TypeResolver();
    private static final ValidationUtils validationUtils = new ValidationUtils();

    /**
     * Gets a value from an object using a dot-notation path.
     *
     * @param root The root object to traverse
     * @param path The dot-notation path to the desired property
     * @return The value at the specified path
     * @throws ReflectionException if the path is invalid or inaccessible
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object root, String path) throws ReflectionException {
        validationUtils.validateInput(root, path);

        String[] parts = path.split("\\.");
        Object current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validationUtils.validatePathSegment(part);

            current = pathTraverser.traversePath(current, part);
        }

        String finalPart = parts[parts.length - 1];
        validationUtils.validatePathSegment(finalPart);

        if (current instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) current;
            return (T) map.get(finalPart);
        }

        try {
            return (T) propertyAccessor.getPropertyValue(current, finalPart);
        } catch (Exception e) {
            throw new ReflectionException("Error getting value at path: " + path, e);
        }
    }

    /**
     * Sets a value in an object using a dot-notation path.
     *
     * @param root The root object to traverse
     * @param path The dot-notation path to the desired property
     * @param value The value to set
     * @throws ReflectionException if the path is invalid or inaccessible
     */
    public static <T> void set(Object root, String path, T value) throws ReflectionException {
        validationUtils.validateInput(root, path);

        String[] parts = path.split("\\.");
        Object current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validationUtils.validatePathSegment(part);

            current = pathTraverser.traversePathAndCreateIfNeeded(current, part);
        }

        String finalPart = parts[parts.length - 1];
        validationUtils.validatePathSegment(finalPart);

        try {
            propertyAccessor.setValueOnObject(current, finalPart, value);
        } catch (Exception e) {
            throw new ReflectionException("Error setting value at path: " + path, e);
        }
    }

    /**
     * Copies a property value from source to target object.
     *
     * @param source Source object to copy from
     * @param target Target object to copy to
     * @param path Property path to copy
     * @throws ReflectionException if types are incompatible or property not found
     */
    public static void copy(Object source, Object target, String path) throws ReflectionException {
        if (source == null || target == null) {
            throw new ReflectionException("Source and target objects cannot be null");
        }

        // First check if source and target are of compatible types
        if (!source.getClass().equals(target.getClass())) {
            throw new ReflectionException(String.format(
                    "Source type %s and target type %s are incompatible",
                    source.getClass().getName(), target.getClass().getName()));
        }

        try {
            Class<?> sourceType = typeResolver.resolveType(source, path);
            Class<?> targetType = typeResolver.resolveType(target, path);

            if (!typeResolver.isCompatibleType(sourceType, targetType)) {
                throw new ReflectionException(String.format(
                        "Type mismatch: cannot copy from %s to %s", sourceType.getName(), targetType.getName()));
            }

            Object value = get(source, path);
            set(target, path, value);
        } catch (Exception e) {
            throw new ReflectionException("Failed to copy property: " + e.getMessage(), e);
        }
    }

    /**
     * Sets a value in an object using a dot-notation path, converting the string value to the
     * appropriate type.
     *
     * @param root The root object to traverse
     * @param path The dot-notation path to the desired property
     * @param valueStr The string value to convert and set
     * @throws ReflectionException if the path is invalid or inaccessible
     */
    public static void setFromString(Object root, String path, String valueStr) throws ReflectionException {
        validationUtils.validateInput(root, "root");
        validationUtils.validateInput(path, "path");
        validationUtils.validateInput(valueStr, "valueStr");

        try {
            Class<?> targetType = typeResolver.resolveType(root, path);
            Object value = typeResolver.parseValueByType(targetType, valueStr);
            set(root, path, value);
        } catch (Exception e) {
            throw new ReflectionException("Error setting value from string at path: " + path, e);
        }
    }
}
