package com.example.linkup.controller;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.service.TaskGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-group")
public class TaskGroupController {
    // TODO: 查询某个人的所有群组(具体URL可能要和获取某id群组的URL区分一下)
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
    public ResponseEntity<TaskGroup> getTaskGroupById(@PathVariable("id") Long id) throws ElementNotExistException {
        System.out.println("1");
        TaskGroup taskGroup = taskGroupService.getTaskGroupById(id);
        if (taskGroup == null) {
            throw new ElementNotExistException("此ID对应的任务群组不存在！");
        }
        return ResponseEntity.ok(taskGroup);
    }

    // 创建任务群组
    @PostMapping
    public ResponseEntity<TaskGroup> createTaskGroup(@RequestBody TaskGroup taskGroup) {
        TaskGroup createdTaskGroup = taskGroupService.createTaskGroup(taskGroup);
        return new ResponseEntity<>(createdTaskGroup, HttpStatus.CREATED);
    }

    // 更新任务群组
    @PutMapping("/{id}")
    public ResponseEntity<TaskGroup> updateTaskGroup(@PathVariable("id") Long id, @RequestBody TaskGroup taskGroup)
            throws ElementNotExistException {
        TaskGroup updatedTaskGroup = taskGroupService.updateTaskGroup(id, taskGroup);
        return ResponseEntity.ok(updatedTaskGroup);
    }

    // 删除任务群组
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskGroup(@PathVariable("id") Long id) {
        taskGroupService.deleteTaskGroup(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }
}
