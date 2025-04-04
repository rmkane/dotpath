package com.rmkane.dotpath.internal.operations;

import java.util.Map;

import com.rmkane.dotpath.api.DotPathException;

/**
 * Utility class for Map operations.
 */
public class MapOperations {
    /**
     * Gets a value from a Map by key.
     *
     * @param map The Map to get the value from
     * @param key The key to look up
     * @return The value associated with the key
     * @throws DotPathException if the key is missing
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Map<String, Object> map, String key) throws DotPathException {
        if (!map.containsKey(key)) {
            throw new DotPathException("Key not found in map: " + key);
        }
        return (T) map.get(key);
    }

    /**
     * Gets a value from a map, returning a default value if not found.
     *
     * @param mapObj The map object
     * @param key The key to look up
     * @param defaultValue The default value to return if key not found
     * @return The value from the map or the default value
     * @throws DotPathException if the object is not a Map
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueOrDefault(Object mapObj, String key, T defaultValue) throws DotPathException {
        Map<String, Object> map = asMap(mapObj);
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    /**
     * Gets the type of a value in a map, returning Object.class if not found.
     *
     * @param mapObj The map object
     * @param key The key to look up
     * @return The class of the value or Object.class if not found
     * @throws DotPathException if the object is not a Map
     */
    public Class<?> getValueType(Object mapObj, String key) throws DotPathException {
        Object value = getValueOrDefault(mapObj, key, null);
        return value != null ? value.getClass() : Object.class;
    }

    /**
     * Sets a value in a Map.
     *
     * @param map   The Map to set the value in
     * @param key   The key to set
     * @param value The value to set
     */
    public void setValue(Map<String, Object> map, String key, Object value) {
        map.put(key, value);
    }

    /**
     * Checks if an object is a Map.
     *
     * @param obj The object to check
     * @return true if the object is a Map, false otherwise
     */
    public boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    /**
     * Casts an object to a Map.
     *
     * @param obj The object to cast
     * @return The object as a Map
     * @throws DotPathException if the object is not a Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> asMap(Object obj) throws DotPathException {
        if (!isMap(obj)) {
            throw new DotPathException("Object is not a Map: " + obj.getClass().getName());
        }
        return (Map<String, Object>) obj;
    }
}
