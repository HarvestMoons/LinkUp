package com.example.linkup.controller;

package com.example.linkup.controller;

import com.example.linkup.model.ChatMessage;
import com.example.linkup.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;

    public ChatWebSocketController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * 监听 WebSocket "/chat/sendMessage"，并将消息广播到 "/topic/group/{groupId}"
     */
    @MessageMapping("/sendMessage")
    @SendTo("/topic/group/{groupId}")
    public ChatMessage sendMessage(ChatMessage message) throws Exception {
        // 先保存消息到数据库
        return chatMessageService.sendMessage(message.getTaskGroup().getId(), message.getSender().getId(), message.getContent());
    }
}
