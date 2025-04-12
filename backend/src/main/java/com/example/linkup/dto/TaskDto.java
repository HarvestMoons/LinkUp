package com.example.linkup.dto;

import com.example.linkup.model.Task;
import com.example.linkup.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {
    @NotNull
    private String title;

    private Long taskGroupId;

    @NotNull
    private User creator;

    private User assignee;

    private String description;

    @NotNull
    private Task.Priority priority;

    @NotNull
    private Task.Status status;

    private LocalDateTime dueDate;
}
