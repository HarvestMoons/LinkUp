package com.example.linkup.service;

import com.example.linkup.exception.ElementExistedException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.GroupMember;
import com.example.linkup.model.User;
import com.example.linkup.repository.GroupMemberRepository;
import com.example.linkup.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupMemberRepository groupMemberRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, GroupMemberRepository groupMemberRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupMemberRepository = groupMemberRepository;
    }

    public void registerUser(String username, String password) {
        // 检查是否有相同的用户名
        if (userRepository.findByUsername(username) != null) {
            throw new ElementExistedException("用户名已存在！");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setLastActiveTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) throws UnexpectedNullElementException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UnexpectedNullElementException("id #" + id + " 对应的用户不存在！"));
    }

    public void removeUser(long userId) {
        userRepository.deleteById(userId);
    }

    // 获取用户所在的所有群组
    public List<GroupMember> getGroupsForUser(long userId) throws UnexpectedNullElementException {
        //检查对应用户是否存在
        findById(userId);
        return groupMemberRepository.findByUserId(userId);
    }

    public User updateUsername(Long userId, String newUsername) throws UnexpectedNullElementException {
        User user = findById(userId);
        user.setUsername(newUsername);
        return userRepository.save(user);
    }

    public void updatePassword(Long userId, String oldPassword, String newPassword) throws UnexpectedNullElementException {
        User user = findById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误，无法更新");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User updateAvatar(Long userId, Integer avatarId) throws UnexpectedNullElementException {
        User user=findById(userId);
        user.setAvatarId(avatarId);
        return userRepository.save(user);
    }

    public void updateLastActiveTime(User user) {
        user.setLastActiveTime(LocalDateTime.now());
        userRepository.save(user);
    }
}
