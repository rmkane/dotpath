package org.example.reflection.api;

/** Custom exception for reflection-related errors. */
public class ReflectionException extends Exception {
    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
