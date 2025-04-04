package com.rmkane.dotpath.internal.traversal;

import lombok.Value;

/**
 * Represents the context of a property operation, containing both the target object
 * and the property name being accessed. This class provides the necessary context
 * for performing property-related operations during path traversal.
 */
@Value
public class PropertyContext {
    /** The target object containing the property */
    private final Object target;

    /** The name of the property to access */
    private final String propertyName;
}
