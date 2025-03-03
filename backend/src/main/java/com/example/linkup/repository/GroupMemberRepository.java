package com.example.linkup.repository;

import com.example.linkup.model.GroupMember;
import com.example.linkup.model.GroupMemberId;

import com.example.linkup.model.TaskGroup;
import com.example.linkup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    // 根据群组查询所有群组成员
    List<GroupMember> findByTaskGroupId(long taskGroupId);

    // 根据群组和用户查询群组成员
    GroupMember findByTaskGroupIdAndUserId(long taskGroupId, long userId);

    // 查询某用户的所有群组信息
    List<GroupMember> findByUserId(long userId);

    // 根据群组和角色查询指定角色的成员
    List<GroupMember> findByTaskGroupIdAndRole(long taskGroupId, GroupMember.Role role);

    // 根据群组ID和用户ID删除群组成员
    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.taskGroup.id = :groupId AND gm.user.id = :userId")
    void deleteByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    // 检查某用户是否在某群组中
    boolean existsByTaskGroupAndUser(TaskGroup taskGroup, User user);
}
