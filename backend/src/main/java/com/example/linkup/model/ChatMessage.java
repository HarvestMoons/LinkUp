package com.example.linkup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 消息ID

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private TaskGroup taskGroup; // 群组（外键）

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 发送者（外键）

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 消息内容

    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt = LocalDateTime.now(); // 发送时间（默认当前时间）

}
