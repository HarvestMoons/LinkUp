package com.example.linkup.service;

import com.example.linkup.exception.ElementExistedException;
import com.example.linkup.model.FriendRequest;
import com.example.linkup.model.User;
import com.example.linkup.repository.FriendRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;

    public FriendRequestService(FriendRequestRepository friendRequestRepository) {
        this.friendRequestRepository = friendRequestRepository;
    }

    // 发送好友请求
    @Transactional
    public FriendRequest sendFriendRequest(User sender, User receiver) {
        // 检查是否已有好友请求
        FriendRequest existingRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (existingRequest != null) {
            throw new ElementExistedException("好友请求已发送！");
        }

        // 创建新的好友请求
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequest.RequestStatus.PENDING);

        return friendRequestRepository.save(friendRequest); // 保存到数据库
    }

    // 获取某个用户的所有好友请求（例如，查看所有待处理请求）
    public List<FriendRequest> getFriendRequestsByReceiverAndStatus(User receiver, FriendRequest.RequestStatus status) {
        return friendRequestRepository.findByReceiverAndStatus(receiver, status);
    }

    // 接受好友请求
    @Transactional
    public FriendRequest acceptFriendRequest(FriendRequest friendRequest) {
        // 更新请求状态
        friendRequest.setStatus(FriendRequest.RequestStatus.ACCEPTED);
        return friendRequestRepository.save(friendRequest);
    }

    // 拒绝好友请求
    @Transactional
    public FriendRequest rejectFriendRequest(FriendRequest friendRequest) {
        // 更新请求状态
        friendRequest.setStatus(FriendRequest.RequestStatus.REJECTED);
        return friendRequestRepository.save(friendRequest);
    }

    public FriendRequest findById(Long id) {
        return friendRequestRepository.findById(id).orElse(null);
    }
}
