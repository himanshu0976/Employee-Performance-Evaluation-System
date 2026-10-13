package com.eps.exception;

public class AuthenticationException extends AppException {
    public AuthenticationException(String message) {
        super(message, 401);
    }
}
