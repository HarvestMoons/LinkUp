package com.example.linkup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 唯一ID

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private User sender;  // 发送者

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
    private User receiver;  // 接收者

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;  // 请求状态（PENDING, ACCEPTED, REJECTED）

    @JsonIgnore
    @Column(updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    @JsonIgnore
    @Column()
    private LocalDateTime updatedAt;  // 更新时间

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }

    /** 在实体插入前，自动填充 createdAt 和 updatedAt */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** 在实体更新前，自动更新 updatedAt */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}

