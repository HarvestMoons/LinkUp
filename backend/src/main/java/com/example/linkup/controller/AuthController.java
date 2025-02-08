package com.example.linkup.controller;

import com.example.linkup.config.Constant;
import com.example.linkup.dto.AuthRequestDto;
import com.example.linkup.exception.ElementExistedException;
import com.example.linkup.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@RequestMapping(Constant.PUBLIC_AUTH_API)
public class AuthController {
    //todo:添加注销账户功能
    //todo:response的status被忽略

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // 处理用户注册
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody AuthRequestDto authRequestDto) {
        String username = authRequestDto.getUsername();
        String password = authRequestDto.getPassword();
        Map<String, Object> response = new HashMap<>();

        try {
            userService.registerUser(username, password); // 调用服务层处理注册逻辑
            response.put("status", HttpStatus.OK.value()); // HTTP状态码 200
            response.put("message", "Registration successful.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ElementExistedException e) {
            // 重新抛出异常，让全局异常处理器处理
            throw e;
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 业务状态码 500
            response.put("message", "注册失败，请检查网络或稍后重试。"); // 错误信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequestDto authRequestDto) {
        String username = authRequestDto.getUsername();
        String password = authRequestDto.getPassword();
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
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "登录失败，请检查网络或稍后重试。");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
