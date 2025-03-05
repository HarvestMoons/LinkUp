package com.example.linkup.controller;

import com.example.linkup.dto.TaskDto;
import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.Task;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.service.TaskService;
import com.example.linkup.service.TaskGroupService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskGroupService taskGroupService;
    private final ModelMapper modelMapper;

    public TaskController(TaskService taskService, TaskGroupService taskGroupService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.taskGroupService = taskGroupService;
        this.modelMapper = modelMapper;
    }

    /**
     * 创建任务
     * 
     * @param taskDto 任务的数据传输对象
     * @return 创建的任务
     */
    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto) {
        TaskGroup taskGroup = taskGroupService.findById(taskDto.getTaskGroupId());
        Task task = taskService.createTask(modelMapper.map(taskDto, Task.class));
        task.setTaskGroup(taskGroup);
        return ResponseEntity.status(201).body(task); // 返回 201 Created
    }

    /**
     * 获取指定状态和优先级的任务
     * 
     * @param status   任务状态
     * @param priority 任务优先级
     * @return 符合条件的任务列表
     */
    @GetMapping("/status/{status}/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByStatusAndPriority(
            @PathVariable("status") String status,
            @PathVariable("priority") String priority) {

        Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
        Task.Priority taskPriority = Task.Priority.valueOf(priority.toUpperCase());
        List<Task> tasks = taskService.getTasksByStatusAndPriority(taskStatus, taskPriority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 更新任务
     * 
     * @param id      任务ID
     * @param taskDto 任务的数据传输对象
     * @return 更新后的任务
     * @throws ElementNotExistException 如果任务不存在
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody TaskDto taskDto)
            throws ElementNotExistException {
        Task existingTask = taskService.findById(id);
        if (existingTask == null) {
            throw new ElementNotExistException("Task with id " + id + " does not exist.");
        }
        Task updatedTask = taskService.updateTask(id, modelMapper.map(taskDto, Task.class));
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 获取所有任务
     * 
     * @return 任务列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取指定群组内的所有任务
     * 
     * @param groupId 群组ID
     * @return 群组任务列表
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Task>> getTasksByGroup(@PathVariable Long groupId) {
        List<Task> tasks = taskService.getTasksByGroupId(groupId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取指定状态的任务
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Task.Status status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取指定优先级的任务
     * 
     * @param priority 任务优先级
     * @return 任务列表
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable Task.Priority priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 根据标题搜索任务
     * 
     * @param title 任务标题
     * @return 符合条件的任务列表
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Task>> getTasksByTitle(@PathVariable String title) {
        List<Task> tasks = taskService.searchTasksByTitle(title);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取某用户的所有单人任务
     * 
     * @param userId 用户ID
     * @return 任务列表
     */
    @GetMapping("/user/{userId}/personal-tasks")
    public List<Task> getPersonalTasks(@PathVariable Long userId) {
        return taskService.getPersonalTasks(userId);
    }

    /**
     * 获取某用户所在群组的所有任务
     * 
     * @param userId 用户ID
     * @return 任务列表
     */
    @GetMapping("/user/{userId}/group-tasks")
    public List<Task> getGroupTasks(@PathVariable Long userId) {
        return taskService.getGroupTasks(userId);
    }

    /**
     * 删除任务
     * 
     * @param id 任务ID
     * @return 无内容响应
     * @throws ElementNotExistException 如果任务不存在
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) throws ElementNotExistException {
        Task task = taskService.findById(id);
        if (task == null) {
            throw new ElementNotExistException("Task with id " + id + " does not exist.");
        }
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
