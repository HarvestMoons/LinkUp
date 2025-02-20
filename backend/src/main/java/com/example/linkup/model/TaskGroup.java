package com.example.linkup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class TaskGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 群组ID

    @Column(nullable = false)
    private String name;  // 群组名称

    private String description;  // 群组描述

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;  // 创建时间

    @Column(name = "updated_at")
    private Timestamp updatedAt;  // 更新时间

}
