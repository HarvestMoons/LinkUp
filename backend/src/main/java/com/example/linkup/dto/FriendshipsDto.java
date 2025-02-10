package com.example.linkup.dto;

import com.example.linkup.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendshipsDto {

    private User user;     // 当前用户
    private User friend;   // 目标好友

}
