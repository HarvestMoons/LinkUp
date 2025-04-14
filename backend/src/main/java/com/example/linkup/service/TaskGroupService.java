package com.example.linkup.service;

import com.example.linkup.dto.TaskGroupDto;
import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
import com.example.linkup.model.TaskGroup;
import com.example.linkup.repository.TaskGroupRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TaskGroupService {

    private final TaskGroupRepository taskGroupRepository;
    private final ModelMapper modelMapper;

    public TaskGroupService(TaskGroupRepository taskGroupRepository, ModelMapper modelMapper) {
        this.taskGroupRepository = taskGroupRepository;
        this.modelMapper = modelMapper;
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
    public TaskGroup createTaskGroup(TaskGroupDto taskGroupDto) {
        TaskGroup taskGroup = modelMapper.map(taskGroupDto, TaskGroup.class);
        return taskGroupRepository.save(taskGroup);
    }

    // 更新群组
    public TaskGroup updateTaskGroup(Long id, TaskGroupDto taskGroupDto) throws ElementNotExistException {
        TaskGroup taskGroup=modelMapper.map(taskGroupDto, TaskGroup.class);
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

    public TaskGroup updateTaskGroupName(Long id, String name) throws UnexpectedNullElementException {
        TaskGroup taskGroup = findById(id);
        taskGroup.setName(name);
        return taskGroupRepository.save(taskGroup);
    }

    public TaskGroup updateTaskGroupDescription(Long id, String description)
            throws UnexpectedNullElementException {
        TaskGroup taskGroup = findById(id);
        taskGroup.setDescription(description);
        return taskGroupRepository.save(taskGroup);
    }

}
