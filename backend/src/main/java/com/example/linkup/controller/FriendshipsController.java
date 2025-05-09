package com.example.linkup.controller;

import com.example.linkup.dto.FriendshipsDto;
import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.Friendships;
import com.example.linkup.model.User;
import com.example.linkup.service.FriendshipsService;
import com.example.linkup.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
public class FriendshipsController {

    private final FriendshipsService friendshipsService;
    private final UserService userService;

    public FriendshipsController(FriendshipsService friendshipsService, UserService userService) {
        this.friendshipsService = friendshipsService;
        this.userService = userService;
    }

    // 添加好友
    @PostMapping("/add")
    public ResponseEntity<Friendships> addFriend(@RequestBody FriendshipsDto requestDto) {
        User user = requestDto.getUser();
        User friend = requestDto.getFriend();
        Friendships friendship = friendshipsService.addFriend(user, friend);
        return ResponseEntity.ok(friendship);
    }

    // 获取某用户的所有好友
    @GetMapping("/find/{userId}")
    public ResponseEntity<List<Friendships>> getFriends(@PathVariable Long userId) throws UnexpectedNullElementException {
        User user = userService.findById(userId);
        List<Friendships> friends = friendshipsService.getFriends(user);
        return ResponseEntity.ok(friends);
    }

    // 删除好友
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFriend(@RequestBody FriendshipsDto request) throws ElementNotExistException {
        User user = request.getUser();
        User friend = request.getFriend();
        friendshipsService.removeFriend(user, friend);
        return ResponseEntity.noContent().build();
    }
}
