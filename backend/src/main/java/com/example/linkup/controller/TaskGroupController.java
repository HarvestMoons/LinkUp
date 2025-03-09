package com.example.linkup.controller;

import com.example.linkup.dto.TaskGroupDto;
import com.example.linkup.exception.ElementNotExistException;
import com.example.linkup.exception.UnexpectedNullElementException;
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

    /**
     * 获取所有任务群组
     *
     * @return 任务群组列表
     */
    @GetMapping
    public List<TaskGroup> getAllTaskGroups() {
        return taskGroupService.getAllTaskGroups();
    }

    /**
     * 获取一个任务群组
     *
     * @param id 任务群组ID
     * @return 任务群组信息
     * @throws UnexpectedNullElementException 如果任务群组不存在
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskGroup> getTaskGroupById(@PathVariable("id") Long id) throws UnexpectedNullElementException {
        TaskGroup taskGroup = taskGroupService.getTaskGroupById(id);
        return ResponseEntity.ok(taskGroup);
    }

    /**
     * 创建任务群组
     *
     * @param taskGroupDto 任务群组数据传输对象
     * @return 创建的任务群组
     */
    @PostMapping("/create")
    public ResponseEntity<TaskGroup> createTaskGroup(@RequestBody TaskGroupDto taskGroupDto) {
        TaskGroup createdTaskGroup = taskGroupService.createTaskGroup(modelMapper.map(taskGroupDto, TaskGroup.class));
        return new ResponseEntity<>(createdTaskGroup, HttpStatus.CREATED);
    }

    /**
     * 更新任务群组
     *
     * @param id           任务群组ID
     * @param taskGroupDto 任务群组数据传输对象
     * @return 更新后的任务群组
     * @throws ElementNotExistException 如果任务群组不存在
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskGroup> updateTaskGroup(@PathVariable("id") Long id, @RequestBody TaskGroupDto taskGroupDto)
            throws ElementNotExistException {
        TaskGroup updatedTaskGroup = taskGroupService.updateTaskGroup(id, modelMapper.map(taskGroupDto, TaskGroup.class));
        return ResponseEntity.ok(updatedTaskGroup);
    }

    /**
     * 删除任务群组
     *
     * @param id 任务群组ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskGroup(@PathVariable("id") Long id) {
        taskGroupService.deleteTaskGroup(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }

    /**
     * 更新任务群组名称
     *
     * @param id   任务群组ID
     * @param name 新的任务群组名称
     * @return 更新后的任务群组
     * @throws UnexpectedNullElementException 如果任务群组不存在
     */
    @PutMapping("/{id}/update-name")
    public ResponseEntity<TaskGroup> updateTaskGroupName(
            @PathVariable("id") Long id,
            @RequestParam String name) throws UnexpectedNullElementException {
        TaskGroup updatedTaskGroup = taskGroupService.updateTaskGroupName(id, name);
        return ResponseEntity.ok(updatedTaskGroup);
    }

    /**
     * 更新任务群组描述
     *
     * @param id          任务群组ID
     * @param description 新的任务群组描述
     * @return 更新后的任务群组
     * @throws UnexpectedNullElementException 如果任务群组不存在
     */
    @PutMapping("/{id}/update-description")
    public ResponseEntity<TaskGroup> updateTaskGroupDescription(
            @PathVariable("id") Long id,
            @RequestParam String description) throws UnexpectedNullElementException {

        TaskGroup updatedTaskGroup = taskGroupService.updateTaskGroupDescription(id, description);
        return ResponseEntity.ok(updatedTaskGroup);
    }


}
