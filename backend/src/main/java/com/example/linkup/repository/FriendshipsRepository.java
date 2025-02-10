package com.example.linkup.repository;

import com.example.linkup.model.Friendships;
import com.example.linkup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipsRepository extends JpaRepository<Friendships, Long> {

    // 查找用户和好友的关系
    Friendships findByUserAndFriend(User user, User friend);

    // 查找某用户的所有好友
    List<Friendships> findByUser(User user);

    // 查找某好友的所有用户关系
    List<Friendships> findByFriend(User friend);
}
