package com.auca.interfaces;

import com.auca.exceptions.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Contract for anything that can be validated, printed and summarised on registration. */
public interface Registrable {
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 60;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MIN_PHONE_LENGTH = 10;
    public static final int MAX_PHONE_LENGTH = 15;
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    void validate() throws ValidationException;
    void printDetails();
    String getRegistrationSummary();

    // ---- Static helper checks, shared by every class and by the forms in Main ----

    /** Text must not be blank and its trimmed length must be between min and max. Returns trimmed text. */
    static String requireText(String value, String label, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(label + " is required (cannot be blank).");
        }
        String t = value.trim();
        if (t.length() < min || t.length() > max) {
            throw new ValidationException(label + " must be " + min + "-" + max + " characters long.");
        }
        return t;
    }

    static String requireEmail(String value, String label) {
        String t = requireText(value, label, 5, MAX_EMAIL_LENGTH);
        if (!t.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException(label + " is not a valid email (example: name@auca.ac.rw).");
        }
        return t;
    }

    /** 10-15 digits with an optional leading +. */
    static String requirePhone(String value, String label) {
        String t = requireText(value, label, 1, 30);
        String digits = t.startsWith("+") ? t.substring(1) : t;
        if (!digits.matches("\\d+") || digits.length() < MIN_PHONE_LENGTH || digits.length() > MAX_PHONE_LENGTH) {
            throw new ValidationException(label + " must contain " + MIN_PHONE_LENGTH + "-" + MAX_PHONE_LENGTH
                    + " digits (optional leading +).");
        }
        return t;
    }

    /** Parses YYYY-MM-DD. Invalid calendar dates (like 2026-02-30) are rejected. */
    static LocalDate parseDate(String value, String label) {
        try {
            return LocalDate.parse(value == null ? "" : value.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date for " + label + ": use " + DATE_PATTERN
                    + " (example: 2004-05-16).");
        }
    }

    static LocalDate requirePast(LocalDate date, String label) {
        requireNotNull(date, label);
        if (!date.isBefore(LocalDate.now())) {
            throw new ValidationException(label + " must be in the past.");
        }
        return date;
    }

    static LocalDate requireNotFuture(LocalDate date, String label) {
        requireNotNull(date, label);
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException(label + " cannot be in the future.");
        }
        return date;
    }

    static void requireNotNull(Object value, String label) {
        if (value == null) {
            throw new ValidationException(label + " is required.");
        }
    }

    static String requireOneOf(String value, String label, List<String> allowed) {
        if (value == null || !allowed.contains(value)) {
            throw new ValidationException(label + " must be one of " + allowed + ".");
        }
        return value;
    }
}
