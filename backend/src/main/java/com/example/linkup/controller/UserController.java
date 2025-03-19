package com.example.linkup.controller;

import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.GroupMember;
import com.example.linkup.model.User;
import com.example.linkup.service.GroupMemberService;
import com.example.linkup.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final GroupMemberService groupMemberService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, GroupMemberService groupMemberService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.groupMemberService = groupMemberService;
        this.passwordEncoder = passwordEncoder;
    }

    // TODO: 尽量只给前端拿到以及使用userid
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
    @DeleteMapping("/close-account/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable long userId) {
        userService.removeUser(userId);
        return ResponseEntity.noContent().build(); // No Content 状态表示删除成功
    }

    // 获取用户加入的所有群组
    @GetMapping("/groups/{userId}")
    public ResponseEntity<List<GroupMember>> getGroupsForUser(@PathVariable long userId) {
        List<GroupMember> groups = groupMemberService.getGroupsForUser(userId);
        return ResponseEntity.ok(groups);
    }

    /**
     * 更新用户名
     *
     * @param userId      用户ID
     * @param newUsername 新用户名
     * @return 更新后的用户信息
     */
    @PutMapping("/update-username/{userId}")
    public ResponseEntity<User> updateUsername(@PathVariable Long userId, @RequestParam String newUsername)
            throws UnexpectedNullElementException {
        User updatedUser = userService.updateUsername(userId, newUsername);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 更新密码
     *
     * @param userId      用户ID
     * @param oldPassword 新密码
     * @param newPassword 新密码
     * @return 更新成功消息
     */
    @PutMapping("/update-password/{userId}")
    public ResponseEntity<String> updatePassword(@PathVariable Long userId,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword)
            throws UnexpectedNullElementException {
        try {
            userService.updatePassword(userId, oldPassword, newPassword);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok("密码更新成功");
    }

    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param avatarId 头像ID
     * @return 更新后的用户信息
     * @throws UnexpectedNullElementException 如果用户不存在
     */
    @PutMapping("/{userId}/update-avatar")
    public ResponseEntity<User> updateAvatar(
            @PathVariable Long userId,
            @RequestParam Integer avatarId) throws UnexpectedNullElementException {
        User updatedUser = userService.updateAvatar(userId, avatarId);
        return ResponseEntity.ok(updatedUser);
    }

}
