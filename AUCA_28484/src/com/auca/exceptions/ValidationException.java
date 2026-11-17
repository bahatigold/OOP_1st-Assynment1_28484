package com.auca.exceptions;

/** Custom exception: thrown when a business rule fails. Unchecked (extends RuntimeException). */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
