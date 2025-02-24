package com.example.linkup.service;

import com.example.linkup.exception.ElementExistedException;
import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.Friendships;
import com.example.linkup.model.User;
import com.example.linkup.repository.FriendshipsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FriendshipsService {

    private final FriendshipsRepository friendshipsRepository;

    public FriendshipsService(FriendshipsRepository friendshipsRepository) {
        this.friendshipsRepository = friendshipsRepository;
    }

    // 添加好友：确保无重复
    public Friendships addFriend(User user, User friend) {
        Friendships existingFriendship = findFriendship(user, friend);
        if (existingFriendship != null) {
            throw new ElementExistedException("Already friends.");
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
    // 注：数据库的约束已经确保了(user, friend)组合的唯一性
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
        List<Friendships> friendList = friendshipsRepository.findByUser(user);
        friendList.addAll(friendshipsRepository.findByFriend(user));
        return friendList;
    }

    // 删除好友
    public void removeFriend(User user, User friend) throws ElementNotExistException {
        Friendships friendship = findFriendship(user, friend);
        if (friendship != null) {
            friendshipsRepository.delete(friendship);
        } else {
            throw new ElementNotExistException("Friendship not found.");
        }
    }
}
