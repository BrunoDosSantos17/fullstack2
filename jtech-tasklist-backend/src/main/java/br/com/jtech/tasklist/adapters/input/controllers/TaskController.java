package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.dtos.TaskDTO;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping
    public ResponseEntity<TaskDTO> create(@RequestBody Task task, @RequestHeader("X-User-Id") String userId) {
        if (!task.getUserId().equals(userId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        return ResponseEntity.ok(mapToDTO(service.create(task)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable String id, @RequestHeader("X-User-Id") String userId) {
        return service.getById(id, userId)
                .map(this::mapToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(service.getByUser(userId).stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<TaskDTO>> getByList(@PathVariable String listId, @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.getByList(listId, userId).stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAll(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.getByUser(userId).stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable String id, @RequestBody Task task, @RequestHeader("X-User-Id") String userId) {
        task.setId(id);
        return ResponseEntity.ok(mapToDTO(service.update(task, userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @RequestHeader("X-User-Id") String userId) {
        service.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> toggleCompleted(@PathVariable String id, @RequestBody ToggleRequest req, @RequestHeader("X-User-Id") String userId) {
        Task updated = service.toggleCompleted(id, req.completed, userId);
        return ResponseEntity.ok(mapToDTO(updated));
    }

    public static class ToggleRequest {
        public boolean completed;
    }

    protected TaskDTO mapToDTO(Task task) {
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate() != null ? task.getDueDate().toString() : null, task.isCompleted(), task.getUserId(), task.getListId());
    }
}
