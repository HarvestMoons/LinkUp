package com.example.linkup.controller;

import com.example.linkup.config.ApiConstant;
import com.example.linkup.dto.AuthRequestDto;
import com.example.linkup.exception.ElementExistedException;
import com.example.linkup.service.UserService;
import com.example.linkup.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiConstant.PUBLIC_AUTH_API)
public class AuthController {

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
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequestDto authRequestDto,
            HttpServletResponse response) {
        String username = authRequestDto.getUsername();
        String password = authRequestDto.getPassword();
        Map<String, Object> responseBody = new HashMap<>();
        try {
            // 通过 Spring Security 进行身份验证
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            authentication = authenticationManager.authenticate(authentication);

            // 将认证信息保存到 SecurityContext 中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtUtils.generateJwtToken(authentication);

            // 设置 HttpOnly Cookie，防止 XSS 访问
            Cookie cookie = new Cookie("jwt", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // 本地调试时为 false，部署到 HTTPS 服务器时设为 true
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 1 天

            response.addCookie(cookie);

            responseBody.put("token", jwtToken);
            responseBody.put("message", "登录成功");
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (AuthenticationException e) {
            // 重新抛出异常，让全局异常处理器处理
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 立即过期
        response.addCookie(cookie);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "登出成功");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
