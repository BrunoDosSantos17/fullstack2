package br.com.jtech.tasklist.adapters.input.controllers.task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "barerAuth")
@Tag(name = "Tasks", description = "Task management APIs")
public class TaskController {


    /*
    * Create new task
    */
    @PostMapping
    @Operation(summary = "Create task", description = "New task for the authenticated user")
    public ResponseEntity<String> createTask(@Valid @RequestBody String request) {
        var task = request;
        return ResponseEntity.ok(task);
    }


    /*
     * Return all task
     */
    @GetMapping
    public ResponseEntity<String> getAllTasks() {
        return ResponseEntity.ok("All tasks");
    }


    /*
    * Update task
    *
    * */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable String id,
                                                   @Valid @RequestBody String request ) {
        var task = request;
        return ResponseEntity.ok(task);
    }

    /**
     * Deletes a task.
     *
     * @param id the task ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        return ResponseEntity.noContent().build();
    }

}
