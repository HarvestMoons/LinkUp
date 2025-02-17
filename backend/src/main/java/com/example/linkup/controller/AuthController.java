package com.example.linkup.controller;

import com.example.linkup.config.ApiConstant;
import com.example.linkup.dto.AuthRequestDto;
import com.example.linkup.exception.ElementExistedException;
import com.example.linkup.model.User;
import com.example.linkup.service.UserService;
import com.example.linkup.util.JwtUtils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@RequestMapping(ApiConstant.PUBLIC_AUTH_API)
public class AuthController {
    // todo:添加注销账户功能
    // todo:response的status被忽略

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    // 处理用户注册
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody AuthRequestDto authRequestDto) {
        String username = authRequestDto.getUsername();
        String password = authRequestDto.getPassword();
        Map<String, Object> response = new HashMap<>();

        try {
            userService.registerUser(username, password); // 调用服务层处理注册逻辑
            response.put("message", "Registration successful.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ElementExistedException e) {
            // 重新抛出异常，让全局异常处理器处理
            throw e;
        } catch (Exception e) {
            response.put("message", "注册失败，请检查网络或稍后重试。"); // 错误信息
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
            String jwtToken = jwtUtils.generateJwtToken(authentication);
            // 登录成功
            response.put("message", "Login successful.");
            response.put("token", jwtToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AuthenticationException e) {
            // 重新抛出异常，让全局异常处理器处理
            throw e;
        } catch (Exception e) {
            response.put("message", "登录失败，请检查网络或稍后重试。");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取当前用户的用户名
    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            // 返回当前登录用户信息
            User user = userService.findByUsername(userDetails.getUsername());
            if (user != null) {
                return ResponseEntity.ok(user); // 返回 HTTP 200 和用户信息
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 用户不存在时返回 404
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 未登录时返回 401
        }
    }
}
