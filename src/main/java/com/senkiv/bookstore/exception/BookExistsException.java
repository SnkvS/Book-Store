package com.senkiv.bookstore.exception;

public class BookExistsException extends RuntimeException {
    public BookExistsException(String message) {
        super(message);
    }

    public BookExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
