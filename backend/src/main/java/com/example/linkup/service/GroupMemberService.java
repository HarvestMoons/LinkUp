package com.example.linkup.service;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.GroupMember;
import com.example.linkup.model.GroupMember.Role;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.model.User;
import com.example.linkup.repository.GroupMemberRepository;
import com.example.linkup.repository.TaskGroupRepository;
import com.example.linkup.repository.UserRepository;
import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final UserRepository userRepository;

    public GroupMemberService(GroupMemberRepository groupMemberRepository, TaskGroupRepository taskGroupRepository, UserRepository userRepository) {
        this.groupMemberRepository = groupMemberRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.userRepository = userRepository;
    }

    // 获取群组的所有成员
    public List<GroupMember> getAllMembers(long taskGroupId) {
        return groupMemberRepository.findByTaskGroupId(taskGroupId);
    }

    // 获取用户所在的所有群组
    public List<GroupMember> getGroupsForUser(long userId) {
        return groupMemberRepository.findByUserId(userId);
    }

    // 获取群组中的特定角色成员
    public List<GroupMember> getMembersByRole(long taskGroupId, Role role) {
        return groupMemberRepository.findByTaskGroupIdAndRole(taskGroupId, role);
    }

    // 添加成员到群组
    public GroupMember addMemberToGroup(TaskGroup taskGroup, User user, @Nullable Role role) {
        GroupMember groupMember = new GroupMember();
        groupMember.setTaskGroup(taskGroup);
        groupMember.setUser(user);
        if (role != null) {
            groupMember.setRole(role);
        }
        return groupMemberRepository.save(groupMember);
    }

    // 更新群组成员角色
    public GroupMember updateMemberRole(long taskGroupId,long userId, Role newRole) throws ElementNotExistException {
        GroupMember groupMember = groupMemberRepository.findByTaskGroupIdAndUserId(taskGroupId,userId);
        if (groupMember == null) {
            throw new ElementNotExistException("指定的用户不存在！");
        }
        groupMember.setRole(newRole);
        return groupMemberRepository.save(groupMember);
    }

    /**
     * 判断某个用户是否在某个群组中
     * @param groupId 群组ID
     * @param userId 用户ID
     * @return true/false
     */
    public boolean isUserInGroup(Long groupId, Long userId) throws ElementNotExistException {
        Optional<TaskGroup> taskGroup = taskGroupRepository.findById(groupId);
        Optional<User> user = userRepository.findById(userId);

        if (taskGroup.isEmpty() || user.isEmpty()) {
            throw new ElementNotExistException("群组或用户不存在");
        }

        return groupMemberRepository.existsByTaskGroupAndUser(taskGroup.get(), user.get());
    }

    // 删除群组成员
    public void removeMember(long taskGroupId,long userId){
        groupMemberRepository.deleteByGroupIdAndUserId(taskGroupId,userId);
    }
}
