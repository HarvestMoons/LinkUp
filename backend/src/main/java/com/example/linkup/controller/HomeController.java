package com.example.linkup.controller;

import com.example.linkup.exception.UsernameExistedException;
import com.example.linkup.service.UserService;
import com.example.linkup.config.ApiConstant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    // 处理用户注册
    @PostMapping(ApiConstant.PUBLIC_AUTH_API + "/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> response = new HashMap<>();

        try {
            userService.registerUser(username, password); // 调用服务层处理注册逻辑
            response.put("status", HttpStatus.OK.value()); // HTTP状态码 200
            response.put("message", "Registration successful.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UsernameExistedException e) {
            // 重新抛出异常，让全局异常处理器处理
            throw e;
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 业务状态码 500
            response.put("message", "注册失败，请检查网络或稍后重试。"); // 错误信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping(ApiConstant.PUBLIC_AUTH_API + "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> response = new HashMap<>();
        try {
            // 通过 Spring Security 进行身份验证
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            authentication = authenticationManager.authenticate(authentication);

            // 将认证信息保存到 SecurityContext 中
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 登录成功
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Login successful.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "登录失败，请检查网络或稍后重试。");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
