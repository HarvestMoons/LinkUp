package com.example.linkup.exception;

public class UsernameExistedException extends RuntimeException {
    public UsernameExistedException() {
        super("用户名已存在!");
    }
}