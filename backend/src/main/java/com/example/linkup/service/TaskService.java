package com.example.linkup.service;

import com.example.linkup.model.Task;
import com.example.linkup.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 创建任务
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    // 更新任务
    public Task updateTask(Long id, Task task) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            task.setId(id);
            task.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(task);
        }
        throw new IllegalStateException("Task not found");
    }

    // 获取所有任务
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // 根据任务状态获取任务
    public List<Task> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    // 根据任务优先级获取任务
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    // 根据任务标题查找任务
    public List<Task> searchTasksByTitle(String title) {
        return taskRepository.findByTitleContaining(title);
    }

    // 根据状态和优先级查找任务
    public List<Task> getTasksByStatusAndPriority(Task.Status status, Task.Priority priority) {
        return taskRepository.findByStatusAndPriority(status, priority);
    }

    // 删除任务
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }
}
