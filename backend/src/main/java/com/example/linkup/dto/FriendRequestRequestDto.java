package com.example.linkup.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendRequestRequestDto {
    @NotNull
    private Long senderId;  // 发送者的ID
    @NotNull
    private Long receiverId;  // 接收者的ID
}
