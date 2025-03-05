package com.example.linkup.dto;

import java.time.LocalDateTime;
import com.example.linkup.model.Task;
import com.example.linkup.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskDto {
    @NotNull
    private String title;

    private Long taskGroupId;

    @NotNull
    private User creator;

    private String description;

    @NotNull
    private Task.Priority priority;

    @NotNull
    private Task.Status status;

    private LocalDateTime dueDate;
}
