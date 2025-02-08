package com.example.linkup.repository;

import com.example.linkup.model.FriendRequest;
import com.example.linkup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    // 根据发送者和接收者查找好友请求
    FriendRequest findBySenderAndReceiver(User sender, User receiver);

    // 根据请求状态查找所有好友请求
    List<FriendRequest> findByStatus(FriendRequest.RequestStatus status);

    // 其他自定义查询可以添加这里
}
