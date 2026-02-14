package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskRepository implements TaskRepositoryPort {

    private final SpringDataTaskRepository repository;

    private Task toDomain(TaskEntity e) {
        return Task.builder()
                .id(e.getId().toString())
                .userId(e.getUserId().toString())
                .listId(e.getListId().toString())
                .title(e.getTitle())
                .description(e.getDescription())
                .completed(e.isCompleted())
                .dueDate(e.getDueDate())
                .build();
    }

    private TaskEntity toEntity(Task t) {
        return TaskEntity.builder()
                .id(t.getId() != null ? UUID.fromString(t.getId()) : null)
                .userId(UUID.fromString(t.getUserId()))
                .listId(UUID.fromString(t.getListId()))
                .title(t.getTitle())
                .description(t.getDescription())
                .completed(t.isCompleted())
                .dueDate(t.getDueDate())
                .build();
    }

    @Override
    public Task save(Task task) {
        return toDomain(repository.save(toEntity(task)));
    }

    @Override
    public Task update(Task task) {
        return toDomain(repository.save(toEntity(task)));
    }

    @Override
    public Optional<Task> findById(String id) {
        return repository.findById(UUID.fromString(id)).map(this::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return repository.findAll()
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Task> findAllByUserId(String userId) {
        return repository.findAllByUserId(UUID.fromString(userId))
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Task> findAllByListId(String listId) {
        return repository.findAllByListId(UUID.fromString(listId))
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(UUID.fromString(id));
    }

    @Override
    public boolean existsByTitleAndListId(String title, String listId) {
        return repository.existsByTitleAndListId(title, UUID.fromString(listId));
    }

    @Override
    public boolean existsByListId(String listId) {
        return repository.existsByListId(UUID.fromString(listId));
    }
}
