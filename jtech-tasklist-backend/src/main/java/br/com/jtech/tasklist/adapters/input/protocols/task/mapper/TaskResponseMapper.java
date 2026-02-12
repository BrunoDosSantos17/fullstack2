package br.com.jtech.tasklist.adapters.input.protocols.task.mapper;


import br.com.jtech.tasklist.adapters.input.protocols.task.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskResponseMapper {
    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        return TaskResponse.builder()
                .id(task.getId() != null ? task.getId().toString() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .listName(task.getListName())
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public List<TaskResponse> toResponseList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }
}
