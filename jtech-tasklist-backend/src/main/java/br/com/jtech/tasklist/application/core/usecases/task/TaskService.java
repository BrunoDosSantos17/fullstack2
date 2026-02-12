package br.com.jtech.tasklist.application.core.usecases.task;

import br.com.jtech.tasklist.adapters.input.protocols.task.TaskRequest;
import br.com.jtech.tasklist.adapters.output.repositories.TaskRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.adapters.output.repositories.mapper.TaskMapper;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    /* ========================= CREATE ========================= */

    public Task createTask(TaskRequest request) {
        UUID userId = getCurrentUserId();

        validateDuplicateTitle(userId, request.listName(), request.title());

        Task domain = Task.create(
                request.title(),
                request.description(),
                request.listName(),
                userId
        );

        var entity = taskMapper.toEntity(domain);

        // associar usuário
        entity.setUser(getUserById(userId));

        var saved = taskRepository.save(entity);

        log.info("Task created: {} for user {}", saved.getId(), userId);

        return taskMapper.toDomain(saved);
    }

    /* ========================= READ ========================= */

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        UUID userId = getCurrentUserId();
        return taskMapper.toDomainList(taskRepository.findByUserId(userId));
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByListName(String listName) {
        UUID userId = getCurrentUserId();
        return taskMapper.toDomainList(
                taskRepository.findByUserIdAndListName(userId, listName)
        );
    }

    @Transactional(readOnly = true)
    public Task getTaskById(String taskId) {
        UUID userId = getCurrentUserId();
        var entity = findTaskByIdAndUser(parseUUID(taskId), userId);
        return taskMapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public List<String> getListNames() {
        return taskRepository.findDistinctListNamesByUserId(getCurrentUserId());
    }

    @Transactional(readOnly = true)
    public List<Task> getCompletedTasks() {
        UUID userId = getCurrentUserId();
        return taskMapper.toDomainList(taskRepository.findCompletedByUserId(userId));
    }

    @Transactional(readOnly = true)
    public List<Task> getPendingTasks() {
        UUID userId = getCurrentUserId();
        return taskMapper.toDomainList(taskRepository.findPendingByUserId(userId));
    }

    /* ========================= UPDATE ========================= */

    public Task updateTask(String taskId, TaskRequest request) {
        UUID userId = getCurrentUserId();
        UUID id = parseUUID(taskId);

        var entity = findTaskByIdAndUser(id, userId);

        if (titleOrListChanged(entity, request)) {
            validateDuplicateTitle(userId, request.listName(), request.title());
        }

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setListName(request.listName());

        if (request.completed() != null) {
            entity.setCompleted(request.completed());
        }

        var updated = taskRepository.save(entity);

        log.info("Task updated: {} for user {}", id, userId);

        return taskMapper.toDomain(updated);
    }

    public Task toggleTaskCompletion(String taskId) {
        UUID userId = getCurrentUserId();
        UUID id = parseUUID(taskId);

        var entity = findTaskByIdAndUser(id, userId);

        entity.toggleCompleted();

        var updated = taskRepository.save(entity);

        log.info("Task completion toggled: {} for user {}", id, userId);

        return taskMapper.toDomain(updated);
    }

    /* ========================= DELETE ========================= */

    public void deleteTask(String taskId) {
        UUID userId = getCurrentUserId();
        UUID id = parseUUID(taskId);

        var entity = findTaskByIdAndUser(id, userId);

        taskRepository.delete(entity);

        log.info("Task deleted: {} for user {}", id, userId);
    }

    /* ========================= PRIVATE METHODS ========================= */

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private UUID parseUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid task ID format");
        }
    }

    private UserEntity getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private void validateDuplicateTitle(UUID userId, String listName, String title) {
        if (taskRepository.existsByUserIdAndListNameAndTitle(userId, listName, title)) {
            throw new BusinessException(
                    String.format("Task with title '%s' already exists in list '%s'", title, listName)
            );
        }
    }

    private boolean titleOrListChanged(TaskEntity task, TaskRequest request) {
        return !task.getTitle().equals(request.title())
                || !task.getListName().equals(request.listName());
    }

    private TaskEntity findTaskByIdAndUser(UUID taskId, UUID userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new BusinessException("Task not found or access denied"));
    }
}
