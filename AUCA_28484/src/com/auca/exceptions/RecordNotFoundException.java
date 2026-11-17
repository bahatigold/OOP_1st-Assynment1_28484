package com.auca.exceptions;

/** Custom exception: thrown when a business rule fails. Unchecked (extends RuntimeException). */
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
