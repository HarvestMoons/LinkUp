package com.example.linkup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class GroupMember {

    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    private TaskGroup taskGroup;  // 群组

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;    // 用户

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;    // 用户角色（OWNER、ADMIN 或 MEMBER）

    @Column(name = "joined_at")
    private Timestamp joinedAt;  // 加入时间

    public enum Role {
        OWNER, ADMIN, MEMBER;
    }

}
