package com.example.linkup.repository;

import com.example.linkup.model.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {
    // 你可以在这里添加更多的查询方法
}
