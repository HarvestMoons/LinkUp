package com.example.linkup.service;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.GroupMember;
import com.example.linkup.model.GroupMember.Role;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.model.User;
import com.example.linkup.repository.GroupMemberRepository;
import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public GroupMemberService(GroupMemberRepository groupMemberRepository) {
        this.groupMemberRepository = groupMemberRepository;
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
            throw new ElementNotExistException("指定的用户不存在");
        }
        groupMember.setRole(newRole);
        return groupMemberRepository.save(groupMember);
    }

    // 删除群组成员
    public void removeMember(long taskGroupId,long userId){
        groupMemberRepository.deleteByGroupIdAndUserId(taskGroupId,userId);
    }
}
