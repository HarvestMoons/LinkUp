package com.example.linkup.service;

import com.example.linkup.model.User;

public interface UserService {
    void registerUser(String username, String password);
    User findByUsername(String username);
}
