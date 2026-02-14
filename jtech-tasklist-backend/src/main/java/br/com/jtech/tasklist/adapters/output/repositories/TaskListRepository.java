package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskListEntity;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskListRepository implements TaskListRepositoryPort {

    private final SpringDataTaskListRepository repository;

    private TaskList toDomain(TaskListEntity e) {
        return TaskList.builder()
                .id(e.getId().toString())
                .userId(e.getUserId().toString())
                .name(e.getName())
                .build();
    }

    private TaskListEntity toEntity(TaskList t) {
        return TaskListEntity.builder()
                .id(t.getId() != null ? UUID.fromString(t.getId()) : null)
                .userId(UUID.fromString(t.getUserId()))
                .name(t.getName())
                .build();
    }

    @Override
    public TaskList save(TaskList taskList) {
        return toDomain(repository.save(toEntity(taskList)));
    }

    @Override
    public TaskList update(TaskList taskList) {
        return toDomain(repository.save(toEntity(taskList)));
    }

    @Override
    public Optional<TaskList> findById(String id) {
        return repository.findById(UUID.fromString(id)).map(this::toDomain);
    }

    @Override
    public List<TaskList> findAll() {
        return repository.findAll()
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TaskList> findAllByUserId(String userId) {
        return repository.findAllByUserId(UUID.fromString(userId))
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(UUID.fromString(id));
    }
}
