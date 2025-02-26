package com.example.linkup.controller;

import com.example.linkup.dto.TaskDto;
import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.Task;
import com.example.linkup.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ModelMapper modelMapper;

    //todo:补充group_id相关的搜索方法

    public TaskController(TaskService taskService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.modelMapper = modelMapper;
    }

    // 创建任务
    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto) {
        Task task = taskService.createTask(modelMapper.map(taskDto, Task.class));
        return ResponseEntity.status(201).body(task); // 返回 201 Created
    }

    // 获取指定状态和优先级的任务
    @GetMapping("/status/{status}/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByStatusAndPriority(
            @PathVariable("status") String status,
            @PathVariable("priority") String priority) {

        // 将状态和优先级转换为枚举值
        Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
        Task.Priority taskPriority = Task.Priority.valueOf(priority.toUpperCase());

        // 调用 Service 层来获取符合条件的任务
        List<Task> tasks = taskService.getTasksByStatusAndPriority(taskStatus, taskPriority);

        return ResponseEntity.ok(tasks); // 返回符合条件的任务列表
    }

    // 更新任务
    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody TaskDto taskDto)
            throws UnexpectedNullElementException {
        // 查找现有任务
        Task existingTask = taskService.findById(id);

        if (existingTask == null) {
            throw new UnexpectedNullElementException("Task with id " + id + " does not exist.");
        }
        // 调用服务层更新任务
        Task updatedTask = taskService.updateTask(id, modelMapper.map(taskDto, Task.class));

        return ResponseEntity.ok(updatedTask); // 返回更新后的任务
    }

    // 获取所有任务
    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks); // 返回所有任务列表
    }

    // 获取指定群组内的所有任务
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Task>> getTasksByGroup(@PathVariable Long groupId) {
        List<Task> tasks = taskService.getTasksByGroupId(groupId);
        return ResponseEntity.ok(tasks); // 返回所有任务列表
    }

    // 删除任务
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) throws ElementNotExistException {
        Task task = taskService.findById(id);
        if (task == null) {
            throw new ElementNotExistException("Task with id " + id + " does not exist.");
        }

        taskService.deleteTask(id); // 调用服务层删除任务
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }
}
