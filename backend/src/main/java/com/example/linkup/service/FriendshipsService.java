package com.example.linkup.service;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.Friendships;
import com.example.linkup.model.User;
import com.example.linkup.repository.FriendshipsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendshipsService {

    private final FriendshipsRepository friendshipsRepository;

    @Autowired
    public FriendshipsService(FriendshipsRepository friendshipsRepository) {
        this.friendshipsRepository = friendshipsRepository;
    }

    // 添加好友：确保无重复
    @Transactional
    public Friendships addFriend(User user, User friend) {
        // findFriendship先按 user -> friend 查找，如果没找到，再查 friend -> user
        Friendships existingFriendship = findFriendship(user, friend);
        if (existingFriendship != null) {
            throw new IllegalStateException("Already friends.");
        }

        // 创建好友关系
        Friendships friendship = new Friendships();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setCreatedAt(LocalDateTime.now());
        friendship.setUpdatedAt(LocalDateTime.now());

        return friendshipsRepository.save(friendship);
    }

    // 根据用户和好友查找好友关系，先查 (user, friend)，再查 (friend, user)，可能返回空值
    public Friendships findFriendship(User user, User friend) {
        // 查找顺序 (user, friend)
        Friendships friendship = friendshipsRepository.findByUserAndFriend(user, friend);
        if (friendship != null) {
            return friendship;
        }

        // 如果没找到，查找 (friend, user)
        return friendshipsRepository.findByUserAndFriend(friend, user);
    }

    // 获取某用户的所有好友
    public List<Friendships> getFriends(User user) {
        return friendshipsRepository.findByUser(user);
    }

    // 删除好友
    @Transactional
    public void removeFriend(User user, User friend) throws ElementNotExistException {
        Friendships friendship = findFriendship(user, friend);
        if (friendship != null) {
            friendshipsRepository.delete(friendship);
        } else {
            throw new ElementNotExistException("Friendship not found.");
        }
    }
}
