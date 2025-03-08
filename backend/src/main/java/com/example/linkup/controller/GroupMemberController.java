package com.example.linkup.controller;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.GroupMember;
import com.example.linkup.model.GroupMember.Role;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.model.User;
import com.example.linkup.service.GroupMemberService;
import com.example.linkup.service.TaskGroupService;
import com.example.linkup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups/{taskGroupId}/members") // URL 路径中的群组ID
public class GroupMemberController {
    private final GroupMemberService groupMemberService;
    private final TaskGroupService taskGroupService;
    private final UserService userService;

    public GroupMemberController(GroupMemberService groupMemberService, TaskGroupService taskGroupService,
            UserService userService) {
        this.groupMemberService = groupMemberService;
        this.taskGroupService = taskGroupService;
        this.userService = userService;
    }

    // 获取群组的所有成员
    @GetMapping
    public ResponseEntity<List<GroupMember>> getAllMembers(@PathVariable long taskGroupId) {
        List<GroupMember> members = groupMemberService.getAllMembers(taskGroupId);
        return ResponseEntity.ok(members);
    }

    // 获取群组中特定角色的成员
    @GetMapping("/role/{role}")
    public ResponseEntity<List<GroupMember>> getMembersByRole(@PathVariable long taskGroupId, @PathVariable Role role) {
        List<GroupMember> members = groupMemberService.getMembersByRole(taskGroupId, role);
        return ResponseEntity.ok(members);
    }

    // 添加成员到群组
    @PostMapping("/{userId}")
    public ResponseEntity<GroupMember> addMemberToGroup(
            @PathVariable long taskGroupId,
            @PathVariable @Valid long userId,
            @RequestParam(required = false) Role role) throws UnexpectedNullElementException {

        TaskGroup taskGroup = taskGroupService.getTaskGroupById(taskGroupId);
        User user = userService.findById(userId);
        GroupMember newMember = groupMemberService.addMemberToGroup(taskGroup, user, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }

    // 更新群组成员角色
    @PutMapping("/{userId}/update-role")
    public ResponseEntity<GroupMember> updateMemberRole(
            @PathVariable long taskGroupId,
            @PathVariable long userId,
            @RequestBody Map<String, String> requestBody) throws ElementNotExistException {
        Role newRole = Role.valueOf(requestBody.get("newRole"));
        GroupMember updatedMember = groupMemberService.updateMemberRole(taskGroupId, userId, newRole);
        return ResponseEntity.ok(updatedMember);
    }

    // 删除群组成员
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable long taskGroupId,
            @PathVariable long userId) {
        groupMemberService.removeMember(taskGroupId, userId);
        return ResponseEntity.noContent().build(); // No Content 状态表示删除成功
    }

    // TODO: 需要自定义异常类，用来区分并抛出因为群组不存在导致的异常 (好像好了？)

    /**
     * 判断指定用户是否在指定群组中
     *
     * @param userId 指定的用户ID
     * @return true/false
     */
    @GetMapping("/{userId}/is-member")
    public ResponseEntity<Boolean> isUserInGroup(@PathVariable long taskGroupId, @PathVariable Long userId)
            throws ElementNotExistException {

        boolean isMember = groupMemberService.isUserInGroup(taskGroupId, userId);
        return ResponseEntity.ok(isMember);
    }

    // TODO: 获取某一用户的role
}
