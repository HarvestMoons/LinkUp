package com.example.linkup.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
