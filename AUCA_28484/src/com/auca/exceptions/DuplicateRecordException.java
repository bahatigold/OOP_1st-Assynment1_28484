package com.auca.exceptions;

/** Custom exception: thrown when a business rule fails. Unchecked (extends RuntimeException). */
public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}
