package com.rmkane.dotpath.api;

/**
 * Custom exception for reflection-related errors.
 * This exception is thrown when reflection operations fail, such as when accessing
 * invalid properties or when type conversion fails.
 */
public class ReflectionException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new reflection exception with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public ReflectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new reflection exception with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
