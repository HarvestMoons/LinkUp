package com.example.linkup.controller;

import com.example.linkup.service.UserService;
import com.example.linkup.model.User;

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
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

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
            // NOTE:
            // 就像第74行，在后端往前端返回的信息的status都要是HttpStatus.OK，
            // 不然前端的代码（如LoginPage.vue）第41行会直接在前端抛出异常，
            // 所以只能在后端返回的消息response中的存HttpStatus.INTERNAL_SERVER_ERROR.value()，像第68行
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> response = new HashMap<>();
        try {
            // 调用findByUsername方法查找用户
            User user = userService.findByUsername(username);

            // 通过 Spring Security 进行身份验证
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            authentication = authenticationManager.authenticate(authentication);

            // 将认证信息保存到 SecurityContext 中
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 登录成功
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Login successful.");
            // response.put("user", user); // 可以将用户信息返回，或者返回一个认证 token 等
            return ResponseEntity.status(HttpStatus.OK).body(response);

        }
        // NOTE:
        // 在这里把所有的异常都给用户显示为“登录失败”了，
        // 后续需要看看怎么用AuthenticationException或者ExceptionHandler之类的东西改改，显示“用户名不存在”这种更准确的提示
        catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Login Failed.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        /*
         * catch (Exception e) {
         * response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
         * response.put("message", "Login failed due to an internal error.");
         * return ResponseEntity.status(HttpStatus.OK).body(response);
         * }
         */
    }

    // TODO: 还没实现从home page自动重定向到login page 或者 按照是否登录显示不同的home page
}
