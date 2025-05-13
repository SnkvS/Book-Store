package com.senkiv.bookstore.exception;

public class FieldValidationException extends Exception {
    public FieldValidationException(String message) {
        super(message);
    }

    public FieldValidationException(Throwable cause) {
        super(cause);
    }

    public FieldValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
