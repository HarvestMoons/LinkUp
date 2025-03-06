package com.example.linkup.controller;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.ChatMessage;
import com.example.linkup.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-message")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * 发送群聊消息
     *
     * @param groupId  群组ID
     * @param senderId 发送者ID
     * @param content  消息内容
     * @return ChatMessage
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestParam Long groupId,
            @RequestParam Long senderId,
            @RequestParam String content) throws ElementNotExistException {
        ChatMessage chatMessage = chatMessageService.sendMessage(groupId, senderId, content);
        return ResponseEntity.ok(chatMessage);
    }

    /**
     * 获取某个群组的聊天记录
     *
     * @param groupId 群组ID
     * @return List<ChatMessage>
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByGroup(@PathVariable Long groupId) throws ElementNotExistException {
        List<ChatMessage> messages = chatMessageService.getMessagesByGroup(groupId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取某个用户发送的所有消息
     *
     * @param senderId 发送者ID
     * @return List<ChatMessage>
     */
    @GetMapping("/user/{senderId}")
    public ResponseEntity<List<ChatMessage>> getMessagesBySender(@PathVariable Long senderId) throws UnexpectedNullElementException {
        List<ChatMessage> messages = chatMessageService.getMessagesBySender(senderId);
        return ResponseEntity.ok(messages);
    }
}
