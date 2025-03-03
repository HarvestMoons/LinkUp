package com.example.linkup.controller;

import com.example.linkup.dto.TaskGroupDto;
import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.service.TaskGroupService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-group")
public class TaskGroupController {
    private final TaskGroupService taskGroupService;
    private final ModelMapper modelMapper;

    public TaskGroupController(TaskGroupService taskGroupService, ModelMapper modelMapper) {
        this.taskGroupService = taskGroupService;
        this.modelMapper = modelMapper;
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
    @PostMapping("/create")
    public ResponseEntity<TaskGroup> createTaskGroup(@RequestBody TaskGroupDto taskGroupDto) {
        System.out.println(taskGroupDto.getName());
        System.out.println(taskGroupDto.getDescription());
        TaskGroup createdTaskGroup = taskGroupService.createTaskGroup(modelMapper.map(taskGroupDto, TaskGroup.class));
        return new ResponseEntity<>(createdTaskGroup, HttpStatus.CREATED);
    }

    // 更新任务群组
    @PutMapping("/{id}")
    public ResponseEntity<TaskGroup> updateTaskGroup(@PathVariable("id") Long id, @RequestBody TaskGroupDto taskGroupDto)
            throws ElementNotExistException {
        TaskGroup updatedTaskGroup = taskGroupService.updateTaskGroup(id, modelMapper.map(taskGroupDto, TaskGroup.class));
        return ResponseEntity.ok(updatedTaskGroup);
    }

    // 删除任务群组
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskGroup(@PathVariable("id") Long id) {
        taskGroupService.deleteTaskGroup(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }
}
