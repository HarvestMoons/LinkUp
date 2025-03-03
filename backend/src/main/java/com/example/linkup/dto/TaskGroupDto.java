package com.example.linkup.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class TaskGroupDto {

    @NotNull
    private String name;        // 群组名称

    private String description; // 群组描述
}
