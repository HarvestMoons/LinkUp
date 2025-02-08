package com.example.linkup.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestRequestDto {

    private Long senderId;  // 发送者的ID
    private Long receiverId;  // 接收者的ID
}
