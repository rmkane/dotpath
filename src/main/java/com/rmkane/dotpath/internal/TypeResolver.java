package com.rmkane.dotpath.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.rmkane.dotpath.api.DotPathException;
import com.rmkane.dotpath.internal.operations.MapOperations;
import com.rmkane.dotpath.internal.operations.PropertyOperations;

/**
 * Handles type resolution and conversion.
 */
public class TypeResolver {
    private static final Map<Class<?>, TypeConverter<?>> TYPE_CONVERTERS = new HashMap<>();

    private final MapOperations mapOperations = new MapOperations();
    private final PropertyOperations propertyOperations = new PropertyOperations();
    private final ValidationUtils validationUtils = new ValidationUtils();

    static {
        // Register basic type converters
        registerConverter(Integer.class, Integer::parseInt);
        registerConverter(Long.class, Long::parseLong);
        registerConverter(Double.class, Double::parseDouble);
        registerConverter(Float.class, Float::parseFloat);
        registerConverter(Boolean.class, Boolean::parseBoolean);
        registerConverter(LocalDate.class, str -> LocalDate.parse(str, DateTimeFormatter.ISO_DATE));
        registerConverter(LocalDateTime.class, str -> LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME));
    }

    /**
     * Resolves the type of property at a given path.
     */
    public Class<?> resolveType(Object root, String path) throws Exception {
        String[] parts = path.split("\\.");
        Object current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validationUtils.validatePathSegment(part);

            if (mapOperations.isMap(current)) {
                current = mapOperations.getValueOrDefault(current, part, Object.class);
                continue;
            }

            current = propertyOperations.getPropertyValue(current, part);
        }

        String last = parts[parts.length - 1];
        validationUtils.validatePathSegment(last);

        if (mapOperations.isMap(current)) {
            return mapOperations.getValueType(current, last);
        }

        // For the last segment, always use the declared type from the class
        return resolveTypeForPathSegment(current.getClass(), last);
    }

    /**
     * Resolves the type for a path segment.
     */
    public Class<?> resolveTypeForPathSegment(Class<?> currentClass, String part) throws Exception {
        String getter = "get" + propertyOperations.capitalize(part);
        try {
            Method method = currentClass.getMethod(getter);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            try {
                Field field = currentClass.getDeclaredField(part);
                field.setAccessible(true);
                return field.getType();
            } catch (NoSuchFieldException e2) {
                throw new DotPathException(
                        String.format("Property '%s' not found in class %s", part, currentClass.getName()));
            }
        }
    }

    /**
     * Parses a string value into the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T> T parseValueByType(Class<T> type, String valueStr) throws DotPathException {
        if (valueStr == null) {
            return null;
        }

        // Try registered converters first
        TypeConverter<?> converter = TYPE_CONVERTERS.get(type);
        if (converter != null) {
            try {
                return (T) converter.convert(valueStr);
            } catch (Exception e) {
                throw new DotPathException("Failed to convert value '" + valueStr + "' to type " + type.getName(), e);
            }
        }

        // Fall back to existing conversion logic
        try {
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
            throw new DotPathException("Unsupported type: " + type.getName());
        } catch (Exception e) {
            throw new DotPathException("Failed to convert value '" + valueStr + "' to type " + type.getName(), e);
        }
    }

    /**
     * Checks if two types are compatible for copying.
     */
    public boolean isCompatibleType(Class<?> sourceType, Class<?> targetType) {
        // If target is Object, it can accept any type
        if (targetType == Object.class) {
            return true;
        }

        // Handle primitive type conversions
        if (sourceType.isPrimitive() || targetType.isPrimitive()) {
            return isPrimitiveCompatible(sourceType, targetType);
        }

        // For non-primitive types, check assignability
        return targetType.isAssignableFrom(sourceType);
    }

    /**
     * Checks compatibility between primitive types.
     */
    private boolean isPrimitiveCompatible(Class<?> sourceType, Class<?> targetType) {
        // Convert to wrapper types for comparison
        Class<?> sourceWrapper = sourceType.isPrimitive() ? getWrapperType(sourceType) : sourceType;
        Class<?> targetWrapper = targetType.isPrimitive() ? getWrapperType(targetType) : targetType;

        // Special case: allow numeric conversions
        if (isNumeric(sourceWrapper) && isNumeric(targetWrapper)) {
            return true;
        }

        return targetWrapper.isAssignableFrom(sourceWrapper);
    }

    /**
     * Gets the wrapper type for a primitive type.
     */
    private Class<?> getWrapperType(Class<?> primitiveType) {
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == short.class) return Short.class;
        return primitiveType;
    }

    /**
     * Checks if a type is numeric.
     */
    private boolean isNumeric(Class<?> type) {
        return Number.class.isAssignableFrom(type)
                || type == int.class
                || type == long.class
                || type == double.class
                || type == float.class
                || type == byte.class
                || type == short.class;
    }

    private static void registerConverter(Class<?> type, TypeConverter<?> converter) {
        TYPE_CONVERTERS.put(type, converter);
    }

    @FunctionalInterface
    private interface TypeConverter<T> {
        T convert(String value) throws Exception;
    }
}
