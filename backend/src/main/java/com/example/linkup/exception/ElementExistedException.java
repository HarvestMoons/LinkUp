package com.example.linkup.exception;

public class ElementExistedException extends RuntimeException {
    public ElementExistedException(String message) {
        super(message);
    }
}