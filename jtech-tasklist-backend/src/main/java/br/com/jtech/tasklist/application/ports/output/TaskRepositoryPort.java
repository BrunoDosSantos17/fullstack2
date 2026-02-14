package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);
    Task update(Task task);
    Optional<Task> findById(String id);
    List<Task> findAll();
    List<Task> findAllByUserId(String userId);
    List<Task> findAllByListId(String listId);
    void deleteById(String id);
    boolean existsByTitleAndListId(String title, String listId);
    boolean existsByListId(String listId);
}
