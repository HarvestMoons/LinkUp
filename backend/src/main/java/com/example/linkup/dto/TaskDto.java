package com.example.linkup.dto;

import java.time.LocalDateTime;
import com.example.linkup.model.Task;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Data
public class TaskDto {
    @NotNull
    private String title;

    private String description;

    @NotNull
    private Task.Priority priority;

    @NotNull
    private Task.Status status;

    private LocalDateTime dueDate;
}
