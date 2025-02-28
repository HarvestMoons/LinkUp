package com.example.linkup.controller;

import com.example.linkup.config.ApiConstant;
import com.example.linkup.model.User;
import com.example.linkup.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    // 注销账户
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable long userId) {
        userService.removeUser(userId);
        return ResponseEntity.noContent().build();  // No Content 状态表示删除成功
    }
}
