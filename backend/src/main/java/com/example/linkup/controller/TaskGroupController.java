package com.example.linkup.controller;

import com.example.linkup.model.TaskGroup;
import com.example.linkup.service.TaskGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task-group")
public class TaskGroupController {

    private final TaskGroupService taskGroupService;

    public TaskGroupController(TaskGroupService taskGroupService) {
        this.taskGroupService = taskGroupService;
    }

    // 获取所有任务群组
    @GetMapping
    public List<TaskGroup> getAllTaskGroups() {
        return taskGroupService.getAllTaskGroups();
    }

    // 获取一个任务群组
    @GetMapping("/{id}")
    public ResponseEntity<TaskGroup> getTaskGroupById(@PathVariable("id") Long id) {
        Optional<TaskGroup> taskGroup = taskGroupService.getTaskGroupById(id);
        return taskGroup.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 创建任务群组
    @PostMapping
    public ResponseEntity<TaskGroup> createTaskGroup(@RequestBody TaskGroup taskGroup) {
        TaskGroup createdTaskGroup = taskGroupService.createTaskGroup(taskGroup);
        return new ResponseEntity<>(createdTaskGroup, HttpStatus.CREATED);
    }

    // 更新任务群组
    @PutMapping("/{id}")
    public ResponseEntity<TaskGroup> updateTaskGroup(@PathVariable("id") Long id, @RequestBody TaskGroup taskGroup) {
        TaskGroup updatedTaskGroup = taskGroupService.updateTaskGroup(id, taskGroup);
        return updatedTaskGroup != null ? ResponseEntity.ok(updatedTaskGroup) : ResponseEntity.notFound().build();
    }

    // 删除任务群组
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskGroup(@PathVariable("id") Long id) {
        taskGroupService.deleteTaskGroup(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }
}
