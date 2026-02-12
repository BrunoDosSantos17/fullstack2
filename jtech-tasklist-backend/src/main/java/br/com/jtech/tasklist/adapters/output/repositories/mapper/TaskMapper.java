package br.com.jtech.tasklist.adapters.output.repositories.mapper;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    /* ================= Entity → Domain ================= */

    public Task toDomain(TaskEntity entity) {
        if (entity == null) {
            return null;
        }

        return Task.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getListName(),
                entity.isCompleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUser() != null ? entity.getUser().getId() : null
        );
    }

    public List<Task> toDomainList(List<TaskEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    /* ================= Domain → Entity ================= */

    public TaskEntity toEntity(Task domain) {
        if (domain == null) {
            return null;
        }

        TaskEntity entity = TaskEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .listName(domain.getListName())
                .completed(domain.isCompleted())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        if (domain.getUserId() != null) {
            entity.setUser(
                    UserEntity.builder()
                            .id(domain.getUserId())
                            .build()
            );
        }

        return entity;
    }
}
