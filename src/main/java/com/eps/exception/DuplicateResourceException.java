package com.eps.exception;

public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(String message) {
        super(message, 409);
    }
}
