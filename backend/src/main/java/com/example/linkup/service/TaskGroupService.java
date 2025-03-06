package com.example.linkup.service;

import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.repository.TaskGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public TaskGroup getTaskGroupById(Long id) throws UnexpectedNullElementException {
        return taskGroupRepository.findById(id).orElseThrow(UnexpectedNullElementException::new);
    }

    // 创建新群组
    public TaskGroup createTaskGroup(TaskGroup taskGroup) {
        return taskGroupRepository.save(taskGroup);
    }

    // 更新群组
    public TaskGroup updateTaskGroup(Long id, TaskGroup taskGroup) throws ElementNotExistException {
        if (taskGroupRepository.existsById(id)) {
            taskGroup.setId(id);
            return taskGroupRepository.save(taskGroup);
        } else {
            throw new ElementNotExistException("待更新的任务群组不存在！");
        }
    }

    // 删除群组
    public void deleteTaskGroup(Long id) {
        taskGroupRepository.deleteById(id);
    }

    public TaskGroup findById(Long id) throws UnexpectedNullElementException {
        return taskGroupRepository.findById(id).orElseThrow(UnexpectedNullElementException::new);
    }
}
