package com.example.linkup.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Friendships {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)  // 级联操作配置
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)  // 级联操作配置
    @JoinColumn(name = "friend_id", referencedColumnName = "id", nullable = false)
    private User friend;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime updatedAt;
}
