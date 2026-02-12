
package br.com.jtech.tasklist.adapters.input.controllers.task;

import br.com.jtech.tasklist.adapters.input.protocols.task.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.task.TaskResponse;
import br.com.jtech.tasklist.adapters.input.protocols.task.mapper.TaskResponseMapper;
import br.com.jtech.tasklist.application.core.usecases.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;
    private final TaskResponseMapper  taskResponseMapper;

    @PostMapping
    @Operation(summary = "Create task", description = "Creates a new task for the authenticated user")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        log.info("Received request to create task: {}", request.title());
        var task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseMapper.toResponse(task));
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks for the authenticated user")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("Received request to get all tasks");
        var tasks  = taskService.getAllTasks();
        return ResponseEntity.ok(taskResponseMapper.toResponseList(tasks));
    }

    @GetMapping("/list/{listName}")
    @Operation(summary = "Get tasks by list", description = "Retrieves tasks for a specific list")
    public ResponseEntity<List<TaskResponse>> getTasksByListName(@PathVariable String listName) {
        log.info("Received request to get tasks for list: {}", listName);
        var tasks = taskService.getTasksByListName(listName);
        return ResponseEntity.ok(taskResponseMapper.toResponseList(tasks));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        log.info("Received request to get task: {}", id);
        var task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskResponseMapper.toResponse(task));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates an existing task")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String id,
                                                   @Valid @RequestBody TaskRequest request) {
        log.info("Received request to update task: {}", id);
        var task = taskService.updateTask(id, request);
        return ResponseEntity.ok(taskResponseMapper.toResponse(task));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle task completion", description = "Toggles the completion status of a task")
    public ResponseEntity<TaskResponse> toggleTaskCompletion(@PathVariable String id) {
        log.info("Received request to toggle completion for task: {}", id);
        var task = taskService.toggleTaskCompletion(id);
        return ResponseEntity.ok(taskResponseMapper.toResponse(task));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        log.info("Received request to delete task: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/lists")
    @Operation(summary = "Get list names", description = "Retrieves all distinct list names for the user")
    public ResponseEntity<List<String>> getListNames() {
        log.info("Received request to get list names");
        var listNames = taskService.getListNames();
        return ResponseEntity.ok(listNames);
    }


    @GetMapping("/completed")
    @Operation(summary = "Get completed tasks", description = "Retrieves all completed tasks for the user")
    public ResponseEntity<List<TaskResponse>> getCompletedTasks() {
        log.info("Received request to get completed tasks");
        var tasks = taskService.getCompletedTasks();
        return ResponseEntity.ok(taskResponseMapper.toResponseList(tasks));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending tasks", description = "Retrieves all pending tasks for the user")
    public ResponseEntity<List<TaskResponse>> getPendingTasks() {
        log.info("Received request to get pending tasks");
        var tasks = taskService.getPendingTasks();
        return ResponseEntity.ok(taskResponseMapper.toResponseList(tasks));
    }
}