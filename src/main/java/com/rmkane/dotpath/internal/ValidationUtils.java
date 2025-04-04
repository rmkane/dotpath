package com.rmkane.dotpath.internal;

import com.rmkane.dotpath.api.ReflectionException;

/**
 * Utility class for validating input parameters and path segments.
 * This class provides methods to ensure that inputs are not null or empty.
 */
public class ValidationUtils {
    /**
     * Validates that the input value is not null or empty.
     *
     * @param value The value to validate
     * @param paramName The name of the parameter being validated (used in error messages)
     * @throws ReflectionException if the value is null or, if it's a String, if it's empty
     */
    public void validateInput(Object value, String paramName) throws ReflectionException {
        if (value == null) {
            throw new ReflectionException(paramName + " cannot be null");
        }
        if (value instanceof String stringValue) {
            if (stringValue.trim().isEmpty()) {
                throw new ReflectionException(paramName + " cannot be empty");
            }
        }
    }

    /**
     * Validates that a path segment is not empty.
     *
     * @param segment The path segment to validate
     * @throws ReflectionException if the segment is empty or consists only of whitespace
     */
    public void validatePathSegment(String segment) throws ReflectionException {
        if (segment.trim().isEmpty()) {
            throw new ReflectionException("Path segment cannot be empty");
        }
    }
}
