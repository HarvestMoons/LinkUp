package com.example.linkup.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.linkup.dto.TaskDto;
import com.example.linkup.model.Task;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // 配置全局行为
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true);

        modelMapper.createTypeMap(TaskDto.class, Task.class)
                .addMappings(mapper -> mapper.skip(Task::setId));

        return modelMapper;
    }
}