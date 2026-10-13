package com.eps.util;

import com.eps.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility for input sanitization and validation.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static void validateEmail(String email) {
        requireNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validateScore(BigDecimal score, String criterionName) {
        if (score == null) {
            throw new ValidationException("Score for " + criterionName + " is required.");
        }
        double val = score.doubleValue();
        if (val < 1.0 || val > 5.0) {
            throw new ValidationException("Score for " + criterionName + " must be between 1.0 and 5.0 (got " + val + ").");
        }
    }

    public static void validateDateRange(LocalDate start, LocalDate end, String context) {
        if (start == null || end == null) {
            throw new ValidationException(context + ": Start date and End date are required.");
        }
        if (end.isBefore(start)) {
            throw new ValidationException(context + ": End date cannot be before Start date.");
        }
    }
}
