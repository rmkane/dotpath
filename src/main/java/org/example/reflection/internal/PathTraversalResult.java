package org.example.reflection.internal;

/** Represents the result of traversing a property path. */
public class PathTraversalResult {
    private final Object target;
    private final String propertyName;

    /**
     * Creates a new PathTraversalResult.
     *
     * @param target The object containing the property
     * @param propertyName The name of the property
     */
    public PathTraversalResult(Object target, String propertyName) {
        this.target = target;
        this.propertyName = propertyName;
    }

    /**
     * Gets the target object containing the property.
     *
     * @return The target object
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Gets the name of the property.
     *
     * @return The property name
     */
    public String getPropertyName() {
        return propertyName;
    }
}
