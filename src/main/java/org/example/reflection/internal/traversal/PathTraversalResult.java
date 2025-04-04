package org.example.reflection.internal.traversal;

import lombok.Value;

/** Represents the result of traversing a property path. */
@Value
public class PathTraversalResult {
    private final Object target;
    private final String propertyName;
}
