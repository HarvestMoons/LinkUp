package com.example.linkup.repository;

import com.example.linkup.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 查找某个状态的所有任务
    List<Task> findByStatus(Task.Status status);

    // 根据优先级查找任务
    List<Task> findByPriority(Task.Priority priority);

    // 根据任务标题查找任务
    List<Task> findByTitleContaining(String title);

    // 根据任务状态和优先级查找任务
    List<Task> findByStatusAndPriority(Task.Status status, Task.Priority priority);
}
