package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findAllByUserId(UUID userId);
    List<TaskEntity> findAllByListId(UUID listId);
    boolean existsByTitleAndListId(String title, UUID listId);
    boolean existsByListId(UUID listId);
}
