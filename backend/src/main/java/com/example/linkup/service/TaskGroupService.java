package com.example.linkup.service;

import com.example.linkup.model.TaskGroup;
import com.example.linkup.repository.TaskGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskGroupService {

    private final TaskGroupRepository taskGroupRepository;

    public TaskGroupService(TaskGroupRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    // 获取所有任务群组
    public List<TaskGroup> getAllTaskGroups() {
        return taskGroupRepository.findAll();
    }

    // 获取一个群组
    public Optional<TaskGroup> getTaskGroupById(Long id) {
        return taskGroupRepository.findById(id);
    }

    // 创建新群组
    public TaskGroup createTaskGroup(TaskGroup taskGroup) {
        return taskGroupRepository.save(taskGroup);
    }

    // 更新群组
    public TaskGroup updateTaskGroup(Long id, TaskGroup taskGroup) {
        if (taskGroupRepository.existsById(id)) {
            taskGroup.setId(id);
            return taskGroupRepository.save(taskGroup);
        } else {
            return null;  // 或抛出异常
        }
    }

    // 删除群组
    public void deleteTaskGroup(Long id) {
        taskGroupRepository.deleteById(id);
    }
}
