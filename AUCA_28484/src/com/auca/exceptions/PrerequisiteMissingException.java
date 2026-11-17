package com.auca.exceptions;

/** Custom exception: thrown when a business rule fails. Unchecked (extends RuntimeException). */
public class PrerequisiteMissingException extends RuntimeException {
    public PrerequisiteMissingException(String message) {
        super(message);
    }
}
