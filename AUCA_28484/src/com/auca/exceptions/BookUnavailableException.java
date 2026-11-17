package com.auca.exceptions;

/** Custom exception: thrown when a business rule fails. Unchecked (extends RuntimeException). */
public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super(message);
    }
}
