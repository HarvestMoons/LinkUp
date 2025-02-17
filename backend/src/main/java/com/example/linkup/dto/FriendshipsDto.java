package com.example.linkup.dto;

import com.example.linkup.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendshipsDto {

    @NotNull
    private User user;     // 当前用户
    @NotNull
    private User friend;   // 目标好友

}
