package com.example.linkup.service;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.model.Task;
import com.example.linkup.repository.GroupMemberRepository;
import com.example.linkup.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final GroupMemberRepository groupMemberRepository;

    public TaskService(TaskRepository taskRepository, GroupMemberRepository groupMemberRepository) {
        this.taskRepository = taskRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    // 创建任务
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // 更新任务
    public Task updateTask(Long id, Task task) throws ElementNotExistException {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            task.setId(id);
            return taskRepository.save(task);
        }
        throw new ElementNotExistException("Task not found");
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

    public List<Task> getTasksByGroupId(long groupId) {
        return taskRepository.findByTaskGroupId(groupId);
    }

    // 获取某个用户的所有单人任务
    public List<Task> getPersonalTasks(Long userId) {
        return taskRepository.findByCreatorIdAndTaskGroupIsNull(userId);
    }

    // 获取某个用户所在群组的所有任务
    public List<Task> getGroupTasks(Long userId) {
        // 找到该用户所在的所有群组
        List<Long> groupIds = groupMemberRepository.findByUserId(userId)
                .stream()
                .map(gm -> gm.getTaskGroup().getId()) // 提取 groupId
                .collect(Collectors.toList());

        if (groupIds.isEmpty()) {
            return List.of(); // 该用户没有加入任何群组
        }

        return taskRepository.findByTaskGroupIdIn(groupIds);
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
