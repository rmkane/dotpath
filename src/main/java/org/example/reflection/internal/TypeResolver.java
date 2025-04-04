package org.example.reflection.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Handles type resolution and conversion. */
public class TypeResolver {
    private final PropertyAccessor propertyAccessor = new PropertyAccessor();
    private final ValidationUtils validationUtils = new ValidationUtils();

    /** Resolves the type of property at a given path. */
    public Class<?> resolveType(Object root, String path) throws Exception {
        String[] parts = path.split("\\.");
        Class<?> currentClass = root.getClass();

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validationUtils.validatePathSegment(part);

            if (Map.class.isAssignableFrom(currentClass)) {
                currentClass = Object.class; // assume Object value inside map
                continue;
            }

            currentClass = resolveTypeForPathSegment(currentClass, part);
        }

        String last = parts[parts.length - 1];
        validationUtils.validatePathSegment(last);

        if (Map.class.isAssignableFrom(currentClass)) {
            return Object.class;
        }

        return resolveTypeForPathSegment(currentClass, last);
    }

    /** Resolves the type for a path segment. */
    public Class<?> resolveTypeForPathSegment(Class<?> currentClass, String part) throws Exception {
        String getter = "get" + propertyAccessor.capitalize(part);
        try {
            Method method = currentClass.getMethod(getter);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            Field field = currentClass.getDeclaredField(part);
            field.setAccessible(true);
            return field.getType();
        }
    }

    /** Parses a string value into the specified type. */
    @SuppressWarnings("unchecked")
    public <T> T parseValueByType(Class<T> type, String valueStr) {
        if (type == int.class || type == Integer.class) {
            return (T) Integer.valueOf(Integer.parseInt(valueStr));
        }
        if (type == long.class || type == Long.class) {
            return (T) Long.valueOf(Long.parseLong(valueStr));
        }
        if (type == double.class || type == Double.class) {
            return (T) Double.valueOf(Double.parseDouble(valueStr));
        }
        if (type == boolean.class || type == Boolean.class) {
            return (T) Boolean.valueOf(Boolean.parseBoolean(valueStr));
        }
        if (type == String.class) {
            return (T) valueStr;
        }
        if (type == List.class || type == ArrayList.class) {
            return (T) Arrays.stream(valueStr.split(",")).map(String::trim).collect(Collectors.toList());
        }
        if (type == Object.class) {
            // For Object type, try to determine the most appropriate type
            try {
                return (T) Integer.valueOf(Integer.parseInt(valueStr));
            } catch (NumberFormatException e) {
                try {
                    return (T) Double.valueOf(Double.parseDouble(valueStr));
                } catch (NumberFormatException e2) {
                    if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
                        return (T) Boolean.valueOf(Boolean.parseBoolean(valueStr));
                    } else {
                        return (T) valueStr;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }
}
