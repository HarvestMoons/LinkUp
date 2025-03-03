package com.example.linkup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
public class TaskGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 群组ID

    @Column(nullable = false)
    private String name; // 群组名称

    private String description; // 群组描述

    @JsonIgnore
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @JsonIgnore
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

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
