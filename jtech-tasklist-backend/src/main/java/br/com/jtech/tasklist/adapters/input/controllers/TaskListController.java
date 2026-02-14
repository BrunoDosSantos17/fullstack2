package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.dtos.TaskListDTO;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.core.services.TaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasklists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListService service;

    @PostMapping
    public ResponseEntity<TaskListDTO> create(@RequestBody TaskList taskList) {
        return ResponseEntity.ok(mapToDTO(service.create(taskList)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskListDTO> getById(@PathVariable String id, @RequestHeader("X-User-Id") String userId) {
        return service.getById(id, userId)
                .map(this::mapToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskListDTO>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(service.getByUser(userId).stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<TaskListDTO>> getAll(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.getByUser(userId).stream().map(this::mapToDTO).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskListDTO> update(@PathVariable String id, @RequestBody TaskList taskList, @RequestHeader("X-User-Id") String userId) {
        taskList.setId(id);
        return ResponseEntity.ok(mapToDTO(service.update(taskList, userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @RequestHeader("X-User-Id") String userId) {
        service.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    protected TaskListDTO mapToDTO(TaskList taskList) {
        return new TaskListDTO(taskList.getId(), taskList.getName(), taskList.getUserId());
    }
}
