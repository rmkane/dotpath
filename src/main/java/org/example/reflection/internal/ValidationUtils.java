package org.example.reflection.internal;

import org.example.reflection.ReflectionException;

/**
 * Handles input validation.
 */
public class ValidationUtils {
    /**
     * Validates input parameters.
     */
    public void validateInput(Object value, String paramName) throws ReflectionException {
        if (value == null) {
            throw new ReflectionException(paramName + " cannot be null");
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            throw new ReflectionException(paramName + " cannot be empty");
        }
    }

    /**
     * Validates a path segment.
     */
    public void validatePathSegment(String segment) throws ReflectionException {
        if (segment.trim().isEmpty()) {
            throw new ReflectionException("Path segment cannot be empty");
        }
    }
} 