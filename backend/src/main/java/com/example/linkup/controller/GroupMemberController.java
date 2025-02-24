package com.example.linkup.controller;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.GroupMember;
import com.example.linkup.model.GroupMember.Role;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.model.User;
import com.example.linkup.service.GroupMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{taskGroupId}/members")  // URL 路径中的群组ID
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    // 获取群组的所有成员
    @GetMapping
    public ResponseEntity<List<GroupMember>> getAllMembers(@PathVariable long taskGroupId) {
        List<GroupMember> members = groupMemberService.getAllMembers(taskGroupId);
        return ResponseEntity.ok(members);
    }

    // 获取用户所在的所有群组
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupMember>> getGroupsForUser(@PathVariable long userId) {
        List<GroupMember> groups = groupMemberService.getGroupsForUser(userId);
        return ResponseEntity.ok(groups);
    }

    // 获取群组中特定角色的成员
    @GetMapping("/role/{role}")
    public ResponseEntity<List<GroupMember>> getMembersByRole(@PathVariable long taskGroupId, @PathVariable Role role) {
        List<GroupMember> members = groupMemberService.getMembersByRole(taskGroupId, role);
        return ResponseEntity.ok(members);
    }

    // 添加成员到群组
    @PostMapping
    public ResponseEntity<GroupMember> addMemberToGroup(
            @PathVariable long taskGroupId,
            @RequestBody @Valid User user,  // 假设用户信息通过请求体传递
            @RequestParam(required = false) Role role) {
        TaskGroup taskGroup = new TaskGroup();  // 假设 TaskGroup 是通过某种方式获取的，或直接在请求体中传递
        taskGroup.setId(taskGroupId);

        GroupMember newMember = groupMemberService.addMemberToGroup(taskGroup, user, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }

    // 更新群组成员角色
    @PutMapping("/user/{userId}/role")
    public ResponseEntity<GroupMember> updateMemberRole(
            @PathVariable long taskGroupId,
            @PathVariable long userId,
            @RequestBody Role newRole) throws ElementNotExistException {
        GroupMember updatedMember = groupMemberService.updateMemberRole(taskGroupId, userId, newRole);
        return ResponseEntity.ok(updatedMember);
    }

    // 删除群组成员
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable long taskGroupId,
            @PathVariable long userId) {
        groupMemberService.removeMember(taskGroupId, userId);
        return ResponseEntity.noContent().build();  // No Content 状态表示删除成功
    }
}
