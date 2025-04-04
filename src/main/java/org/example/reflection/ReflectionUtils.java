package org.example.reflection;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {

    // ===== PUBLIC API METHODS =====

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
        validateInput(root, path);
        
        String[] parts = path.split("\\.");
        Object current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validatePathSegment(part);
            
            current = traversePath(current, part);
        }

        String finalPart = parts[parts.length - 1];
        validatePathSegment(finalPart);
        
        if (current instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) current;
            return (T) map.get(finalPart);
        }

        try {
            return (T) getValueFromObject(current, finalPart);
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
        validateInput(root, path);
        
        String[] parts = path.split("\\.");
        Object current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validatePathSegment(part);
            
            current = traversePathAndCreateIfNeeded(current, part);
        }

        String finalPart = parts[parts.length - 1];
        validatePathSegment(finalPart);
        
        try {
            setValueOnObject(current, finalPart, value);
        } catch (Exception e) {
            throw new ReflectionException("Error setting value at path: " + path, e);
        }
    }

    /**
     * Copies a value from one object to another using a dot-notation path.
     * 
     * @param source The source object
     * @param target The target object
     * @param path The dot-notation path to the desired property
     * @throws ReflectionException if the path is invalid or inaccessible
     */
    public static <T> void copy(Object source, Object target, String path) throws ReflectionException {
        validateInput(source, "source");
        validateInput(target, "target");
        validateInput(path, "path");
        
        T value = get(source, path);
        set(target, path, value);
    }

    /**
     * Sets a value in an object using a dot-notation path, converting the string value to the appropriate type.
     * 
     * @param root The root object to traverse
     * @param path The dot-notation path to the desired property
     * @param valueStr The string value to convert and set
     * @throws ReflectionException if the path is invalid or inaccessible
     */
    public static void setFromString(Object root, String path, String valueStr) throws ReflectionException {
        validateInput(root, "root");
        validateInput(path, "path");
        validateInput(valueStr, "valueStr");
        
        try {
            Class<?> targetType = resolveType(root, path);
            Object value = parseValueByType(targetType, valueStr);
            set(root, path, value);
        } catch (Exception e) {
            throw new ReflectionException("Error setting value from string at path: " + path, e);
        }
    }

    // ===== PATH TRAVERSAL METHODS =====

    /**
     * Traverses a path in an object and returns the object at the specified path segment.
     * 
     * @param current The current object
     * @param part The path segment to traverse
     * @return The object at the specified path segment
     * @throws ReflectionException if the path segment is invalid or inaccessible
     */
    private static Object traversePath(Object current, String part) throws ReflectionException {
        Object result = handleMapOrNull(current, part);
        if (result != null) {
            return result;
        }

        try {
            return getPropertyValue(current, part);
        } catch (Exception e) {
            throw new ReflectionException("Error traversing path segment: " + part, e);
        }
    }

    /**
     * Traverses a path in an object and returns the object at the specified path segment,
     * creating intermediate objects if needed.
     * 
     * @param current The current object
     * @param part The path segment to traverse
     * @return The object at the specified path segment
     * @throws ReflectionException if the path segment is invalid or inaccessible
     */
    private static Object traversePathAndCreateIfNeeded(Object current, String part) throws ReflectionException {
        Object result = handleMapOrNull(current, part);
        if (result != null) {
            return result;
        }

        try {
            String getter = "get" + capitalize(part);
            try {
                Method method = current.getClass().getMethod(getter);
                Object next = method.invoke(current);
                if (next == null) {
                    next = createAndSetIntermediateObject(current, part);
                }
                return next;
            } catch (NoSuchMethodException e) {
                Field field = current.getClass().getDeclaredField(part);
                field.setAccessible(true);
                Object next = field.get(current);
                if (next == null) {
                    next = createAndSetIntermediateObject(current, part);
                }
                return next;
            }
        } catch (Exception e) {
            throw new ReflectionException("Error traversing path segment: " + part, e);
        }
    }

    /**
     * Handles null checks and map operations for path traversal.
     * Returns null if the object is not a map or null.
     * 
     * @param current The current object
     * @param part The path segment
     * @return The next object if current is a map, null otherwise
     * @throws ReflectionException if current is null or map key is missing
     */
    private static Object handleMapOrNull(Object current, String part) throws ReflectionException {
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

    // ===== PROPERTY ACCESS METHODS =====

    /**
     * Gets a value from an object using a property name.
     * 
     * @param obj The object to get the value from
     * @param propertyName The property name
     * @return The value of the property
     * @throws Exception if the property cannot be accessed
     */
    private static Object getValueFromObject(Object obj, String propertyName) throws Exception {
        return getPropertyValue(obj, propertyName);
    }

    /**
     * Retrieves a property value from an object using reflection.
     * First attempts to use a getter method (getPropertyName), then falls back to direct field access.
     * 
     * @param obj The object to retrieve the property from
     * @param propertyName The name of the property to retrieve
     * @return The value of the property
     * @throws IllegalAccessException if the property cannot be accessed
     * @throws InvocationTargetException if the getter method throws an exception
     * @throws NoSuchFieldException if neither a getter method nor a field exists for the property
     */
    private static Object getPropertyValue(Object obj, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
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
     * 
     * @param obj The object to set the value on
     * @param propertyName The property name
     * @param value The value to set
     * @throws Exception if the property cannot be accessed
     */
    private static <T> void setValueOnObject(Object obj, String propertyName, T value) throws Exception {
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
     * 
     * @param current The current object
     * @param part The path segment
     * @return The created intermediate object
     * @throws Exception if the object cannot be created or set
     */
    private static Object createAndSetIntermediateObject(Object current, String part) throws Exception {
        Field field = current.getClass().getDeclaredField(part);
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        Object instance = fieldType.getDeclaredConstructor().newInstance();
        field.set(current, instance);
        return instance;
    }

    // ===== TYPE RESOLUTION METHODS =====

    /**
     * Resolves the type of a property at a given path.
     * 
     * @param root The root object
     * @param path The dot-notation path to the property
     * @return The type of the property
     * @throws Exception if the type cannot be resolved
     */
    private static Class<?> resolveType(Object root, String path) throws Exception {
        String[] parts = path.split("\\.");
        Class<?> currentClass = root.getClass();

        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            validatePathSegment(part);
            
            if (Map.class.isAssignableFrom(currentClass)) {
                currentClass = Object.class; // assume Object value inside map
                continue;
            }

            currentClass = resolveTypeForPathSegment(currentClass, part);
        }

        String last = parts[parts.length - 1];
        validatePathSegment(last);
        
        if (Map.class.isAssignableFrom(currentClass)) {
            return Object.class;
        }

        return resolveTypeForPathSegment(currentClass, last);
    }

    /**
     * Resolves the type for a path segment.
     * 
     * @param currentClass The current class
     * @param part The path segment
     * @return The type for the path segment
     * @throws Exception if the type cannot be resolved
     */
    private static Class<?> resolveTypeForPathSegment(Class<?> currentClass, String part) throws Exception {
        String getter = "get" + capitalize(part);
        try {
            Method method = currentClass.getMethod(getter);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            Field field = currentClass.getDeclaredField(part);
            field.setAccessible(true);
            return field.getType();
        }
    }

    // ===== VALUE CONVERSION METHODS =====

    /**
     * Parses a string value into the specified type.
     * 
     * @param type The type to parse the string into
     * @param valueStr The string value to parse
     * @return The parsed value
     * @throws IllegalArgumentException if the type is not supported
     */
    @SuppressWarnings("unchecked")
    private static <T> T parseValueByType(Class<T> type, String valueStr) {
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
            return (T) Arrays.stream(valueStr.split(","))
                         .map(String::trim)
                         .collect(Collectors.toList());
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

    // ===== UTILITY METHODS =====

    /**
     * Validates input parameters.
     * 
     * @param value The value to validate
     * @param paramName The parameter name
     * @throws ReflectionException if the value is invalid
     */
    private static void validateInput(Object value, String paramName) throws ReflectionException {
        if (value == null) {
            throw new ReflectionException(paramName + " cannot be null");
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            throw new ReflectionException(paramName + " cannot be empty");
        }
    }

    /**
     * Validates a path segment.
     * 
     * @param segment The path segment to validate
     * @throws ReflectionException if the segment is invalid
     */
    private static void validatePathSegment(String segment) throws ReflectionException {
        if (segment.trim().isEmpty()) {
            throw new ReflectionException("Path segment cannot be empty");
        }
    }

    /**
     * Capitalizes the first character of a string.
     * 
     * @param s The string to capitalize
     * @return The capitalized string
     */
    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}