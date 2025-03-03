package com.example.linkup.repository;

import com.example.linkup.model.ChatMessage;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 根据群组 ID 获取所有消息（按时间升序排列）
    List<ChatMessage> findByTaskGroupOrderBySentAtAsc(TaskGroup taskGroup);

    // 根据发送者 ID 获取所有消息
    List<ChatMessage> findBySender(User sender);
}
