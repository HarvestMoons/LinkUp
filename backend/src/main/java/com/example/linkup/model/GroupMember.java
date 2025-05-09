package com.example.linkup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@IdClass(GroupMemberId.class) // 这里使用 @IdClass 指定复合主键类
public class GroupMember {

    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    private TaskGroup taskGroup; // 群组

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 用户

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 用户角色（OWNER、ADMIN 或 MEMBER）

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false)
    private Timestamp joinedAt; // groupMe

    public enum Role {
        OWNER, ADMIN, MEMBER;
    }

}
