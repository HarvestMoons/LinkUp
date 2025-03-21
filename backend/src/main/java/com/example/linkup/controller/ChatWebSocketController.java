package com.example.linkup.controller;

import com.example.linkup.model.ChatMessage;
import com.example.linkup.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatMessageService chatMessageService, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 监听 WebSocket "/chat/sendMessage"，并将消息发送到 "/topic/group/{groupId}"
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessage message) throws Exception {
        System.out.println("收到 WebSocket 消息: " + message.getContent());
        System.out.println("任务组 ID：" + message.getTaskGroup().getId());
        ChatMessage savedMessage = chatMessageService.sendMessage(
                message.getTaskGroup().getId(),
                message.getSender().getId(),
                message.getContent());

        // 这里动态构造目标路径
        String destination = "/topic/group/" + message.getTaskGroup().getId();
        messagingTemplate.convertAndSend(destination, savedMessage);
        System.out.println("目标主题：" + destination);
        System.out.println("发送消息内容：" + savedMessage.getContent());
    }
}
