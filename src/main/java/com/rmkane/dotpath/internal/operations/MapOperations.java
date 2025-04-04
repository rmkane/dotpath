package com.rmkane.dotpath.internal.operations;

import java.util.Map;

import com.rmkane.dotpath.api.DotPathException;

/** Handles operations specific to Map objects. */
public class MapOperations {
    /**
     * Gets a value from a map.
     *
     * @param map The map to get the value from
     * @param key The key to get
     * @return The value at the specified key
     * @throws DotPathException if the key is missing
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Map<String, Object> map, String key) throws DotPathException {
        Object value = map.get(key);
        if (value == null && !map.containsKey(key)) {
            throw new DotPathException("Map key missing: " + key);
        }
        return (T) value;
    }

    /**
     * Sets a value in a map.
     *
     * @param map The map to set the value in
     * @param key The key to set
     * @param value The value to set
     */
    public void setValue(Map<String, Object> map, String key, Object value) {
        map.put(key, value);
    }

    /**
     * Checks if an object is a map.
     *
     * @param obj The object to check
     * @return true if the object is a map, false otherwise
     */
    public boolean isMap(Object obj) {
        return obj instanceof Map<?, ?>;
    }

    /**
     * Casts an object to a Map&lt;String, Object&gt;.
     *
     * @param obj The object to cast
     * @return The object as a Map&lt;String, Object&gt;
     * @throws DotPathException if the object is not a Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> asMap(Object obj) throws DotPathException {
        if (!isMap(obj)) {
            throw new DotPathException("Object is not a Map");
        }
        return (Map<String, Object>) obj;
    }
}
