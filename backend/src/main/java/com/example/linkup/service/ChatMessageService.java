package com.example.linkup.service;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.ChatMessage;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.model.User;
import com.example.linkup.repository.ChatMessageRepository;
import com.example.linkup.repository.TaskGroupRepository;
import com.example.linkup.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final UserRepository userRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              TaskGroupRepository taskGroupRepository,
                              UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.userRepository = userRepository;
    }

    /**
     * 发送消息
     *
     * @param groupId  群组ID
     * @param senderId 发送者ID
     * @param content  消息内容
     * @return ChatMessage
     */
    public ChatMessage sendMessage(Long groupId, Long senderId, String content) throws ElementNotExistException {
        Optional<TaskGroup> taskGroup = taskGroupRepository.findById(groupId);
        Optional<User> sender = userRepository.findById(senderId);

        if (taskGroup.isEmpty() || sender.isEmpty()) {
            throw new ElementNotExistException("群组或用户不存在");
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setTaskGroup(taskGroup.get());
        chatMessage.setSender(sender.get());
        chatMessage.setContent(content);

        return chatMessageRepository.save(chatMessage);
    }

    /**
     * 获取某个群组的聊天记录
     *
     * @param groupId 群组ID
     * @return List<ChatMessage>
     */
    public List<ChatMessage> getMessagesByGroup(Long groupId) throws ElementNotExistException {
        Optional<TaskGroup> taskGroup = taskGroupRepository.findById(groupId);
        if (taskGroup.isEmpty()) {
            throw new ElementNotExistException("群组不存在");
        }
        return chatMessageRepository.findByTaskGroupOrderBySentAtAsc(taskGroup.get());
    }

    /**
     * 获取某个用户发送的所有消息
     *
     * @param senderId 发送者ID
     * @return List<ChatMessage>
     */
    public List<ChatMessage> getMessagesBySender(Long senderId) throws UnexpectedNullElementException {
        User sender = userRepository.findById(senderId).orElseThrow(UnexpectedNullElementException::new);
        return chatMessageRepository.findBySender(sender);
    }
}
