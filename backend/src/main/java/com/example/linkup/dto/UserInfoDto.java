package com.example.linkup.dto;

import com.example.linkup.model.User;
import lombok.Getter;
// Getters（此 DTO 是只读的）
@Getter
public class UserInfoDto {
    private final Long id;
    private final String username;
    private final Integer avatarId;

    // 构造函数（从 User 转换）
    public UserInfoDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.avatarId = user.getAvatarId();
    }
}