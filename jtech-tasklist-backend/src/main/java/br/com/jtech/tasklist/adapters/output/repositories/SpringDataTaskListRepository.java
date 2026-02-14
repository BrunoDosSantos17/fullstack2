package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskListRepository extends JpaRepository<TaskListEntity, UUID> {
    List<TaskListEntity> findAllByUserId(UUID userId);
}
