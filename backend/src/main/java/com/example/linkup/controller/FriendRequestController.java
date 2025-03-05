package com.example.linkup.controller;

import com.example.linkup.dto.FriendRequestRequestDto;
import com.example.linkup.exception.ElementNotExistException;
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
            throws ElementNotExistException, UnexpectedNullElementException {
        // 获取发送者和接收者
        User sender = userService.findById(requestDto.getSenderId());
        User receiver = userService.findById(requestDto.getReceiverId());

        if (sender == null) {
            throw new UnexpectedNullElementException();
        }
        if (receiver == null) {
            throw new ElementNotExistException("id #" + requestDto.getReceiverId() + " 对应的用户不存在！");
        }

        FriendRequest friendRequest = friendRequestService.sendFriendRequest(sender, receiver);
        return ResponseEntity.ok(friendRequest);
    }

    @GetMapping("/receiver/{receiverId}/status/{status}")
    public ResponseEntity<List<FriendRequest>> getFriendRequestsByReceiverAndStatus(
            @PathVariable("receiverId") Long receiverId,
            @PathVariable("status") String status) {

        // 将状态转换为 RequestStatus 枚举值
        FriendRequest.RequestStatus requestStatus = FriendRequest.RequestStatus.valueOf(status.toUpperCase());

        // 获取接收者的 User 对象（可以通过 UserService 查询）
        User receiver = userService.findById(receiverId); // 假设 UserService 中有 findById 方法

        // 调用 Service 层来获取接收者和状态的好友请求
        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsByReceiverAndStatus(receiver,
                requestStatus);

        return ResponseEntity.ok(friendRequests); // 返回符合条件的好友请求
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<FriendRequest> acceptFriendRequest(@PathVariable("id") Long id)
            throws UnexpectedNullElementException {
        // 查找特定的好友请求
        FriendRequest friendRequest = friendRequestService.findById(id);

        if (friendRequest == null) {
            throw new UnexpectedNullElementException();
        }

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
    public ResponseEntity<FriendRequest> rejectFriendRequest(@PathVariable("id") Long id) {
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
