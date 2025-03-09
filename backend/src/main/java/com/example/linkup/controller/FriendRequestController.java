package com.example.linkup.controller;

import com.example.linkup.dto.FriendRequestRequestDto;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.FriendRequest;
import com.example.linkup.model.User;
import com.example.linkup.service.FriendRequestService;
import com.example.linkup.service.FriendshipsService;
import com.example.linkup.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;
    private final FriendshipsService friendshipsService;

    public FriendRequestController(FriendRequestService friendRequestService, UserService userService,
                                   FriendshipsService friendshipsService) {
        this.friendRequestService = friendRequestService;
        this.userService = userService;
        this.friendshipsService = friendshipsService;
    }

    // 发送好友请求
    @PostMapping("/send")
    public ResponseEntity<FriendRequest> sendFriendRequest(@RequestBody FriendRequestRequestDto requestDto)
            throws UnexpectedNullElementException {
        User sender = userService.findById(requestDto.getSenderId());
        User receiver = userService.findById(requestDto.getReceiverId());
        FriendRequest friendRequest = friendRequestService.sendFriendRequest(sender, receiver);
        return ResponseEntity.ok(friendRequest);
    }

    @GetMapping("/receiver/{receiverId}/status/{status}")
    public ResponseEntity<List<FriendRequest>> getFriendRequestsByReceiverAndStatus(
            @PathVariable("receiverId") Long receiverId,
            @PathVariable("status") String status) throws UnexpectedNullElementException {

        // 将状态转换为 RequestStatus 枚举值
        FriendRequest.RequestStatus requestStatus = FriendRequest.RequestStatus.valueOf(status.toUpperCase());

        User receiver = userService.findById(receiverId);

        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsByReceiverAndStatus(receiver,
                requestStatus);

        return ResponseEntity.ok(friendRequests); // 返回符合条件的好友请求
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<FriendRequest> acceptFriendRequest(@PathVariable("id") Long id)
            throws UnexpectedNullElementException {
        // 查找特定的好友请求
        FriendRequest friendRequest = friendRequestService.findById(id);

        // 判断请求是否为待处理状态
        if (friendRequest.getStatus() != FriendRequest.RequestStatus.PENDING) {
            return ResponseEntity.badRequest().build(); // 如果不是待处理状态，返回错误响应
        }

        // 调用服务层来接受好友请求
        FriendRequest updatedRequest = friendRequestService.acceptFriendRequest(friendRequest);

        // 好友请求通过后，调用 service 层方法添加好友关系
        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();
        friendshipsService.addFriend(sender, receiver);

        return ResponseEntity.ok(updatedRequest);
    }

    // 拒绝好友请求
    @PostMapping("/reject/{id}")
    public ResponseEntity<FriendRequest> rejectFriendRequest(@PathVariable("id") Long id) throws UnexpectedNullElementException {
        // 查找特定的好友请求
        FriendRequest friendRequest = friendRequestService.findById(id);

        // 判断请求是否为待处理状态
        if (friendRequest.getStatus() != FriendRequest.RequestStatus.PENDING) {
            return ResponseEntity.badRequest().build(); // 如果不是待处理状态，返回错误响应
        }

        // 调用服务层来拒绝好友请求
        FriendRequest updatedRequest = friendRequestService.rejectFriendRequest(friendRequest);
        return ResponseEntity.ok(updatedRequest);
    }

}
