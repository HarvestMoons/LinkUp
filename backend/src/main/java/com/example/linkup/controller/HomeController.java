package com.example.linkup.controller;

import com.example.linkup.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    /*
     * // 显示注册页面
     * 
     * @GetMapping("/register")
     * public String registerPage() {
     * return "register"; // 返回注册页面
     * }
     * 
     * // 显示登录页面
     * 
     * @GetMapping("/login")
     * public String loginPage() {
     * return "login"; // 返回登录页面
     * }
     * 
     * @GetMapping("/home")
     * public String home() {
     * return "home"; // 返回 home.html 视图
     * }
     */
    // 处理用户注册
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> response = new HashMap<>();
        try {
            userService.registerUser(username, password); // 调用服务层处理注册逻辑
            response.put("status", HttpStatus.OK.value()); // HTTP状态码 200
            response.put("message", "Registration successful.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // HTTP状态码 500
            response.put("message", "Registration failed.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
