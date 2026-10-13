package com.eps.exception;

import java.util.Collections;
import java.util.List;

public class ValidationException extends AppException {
    private final List<String> errors;

    public ValidationException(String message) {
        super(message, 400);
        this.errors = Collections.singletonList(message);
    }

    public ValidationException(List<String> errors) {
        super(errors != null && !errors.isEmpty() ? String.join(", ", errors) : "Validation failed", 400);
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public List<String> getErrors() {
        return errors;
    }
}
