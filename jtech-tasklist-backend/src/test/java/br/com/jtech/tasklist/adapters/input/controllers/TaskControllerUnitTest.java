package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.dtos.TaskDTO;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.services.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerUnitTest {

    @Mock
    private TaskService service;

    @InjectMocks
    private TaskController controller;

    @Test
    @DisplayName("Criar tarefa com userId válido retorna OK")
    void createTaskSuccess() {
        Task task = Task.builder().userId("user1").title("Test").build();
        when(service.create(any(Task.class))).thenReturn(task);

        ResponseEntity<TaskDTO> response = controller.create(task, "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(service).create(task);
    }

    @Test
    @DisplayName("Criar tarefa com userId diferente retorna Forbidden")
    void createTaskForbidden() {
        Task task = Task.builder().userId("user1").title("Test").build();

        ResponseEntity<TaskDTO> response = controller.create(task, "user2");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("Buscar tarefa por ID existente retorna OK")
    void getByIdFound() {
        Task task = Task.builder().id("1").userId("user1").title("Test").completed(false).build();
        when(service.getById("1", "user1")).thenReturn(Optional.of(task));

        ResponseEntity<TaskDTO> response = controller.getById("1", "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo("1");
    }

    @Test
    @DisplayName("Buscar tarefa por ID inexistente retorna Not Found")
    void getByIdNotFound() {
        when(service.getById("999", "user1")).thenReturn(Optional.empty());

        ResponseEntity<TaskDTO> response = controller.getById("999", "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Buscar tarefas por usuário retorna lista")
    void getByUser() {
        List<Task> tasks = List.of(Task.builder().id("1").userId("user1").completed(false).build());
        when(service.getByUser("user1")).thenReturn(tasks);

        ResponseEntity<List<TaskDTO>> response = controller.getByUser("user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Buscar tarefas por lista retorna lista filtrada")
    void getByList() {
        List<Task> tasks = List.of(Task.builder().id("1").listId("list1").completed(false).build());
        when(service.getByList("list1", "user1")).thenReturn(tasks);

        ResponseEntity<List<TaskDTO>> response = controller.getByList("list1", "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Buscar todas as tarefas retorna lista do usuário")
    void getAll() {
        List<Task> tasks = List.of(Task.builder().id("1").userId("user1").completed(false).build());
        when(service.getByUser("user1")).thenReturn(tasks);

        ResponseEntity<List<TaskDTO>> response = controller.getAll("user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Atualizar tarefa retorna tarefa atualizada")
    void updateTask() {
        Task task = Task.builder().id("1").userId("user1").title("Updated").completed(false).build();
        when(service.update(any(Task.class), eq("user1"))).thenReturn(task);

        ResponseEntity<TaskDTO> response = controller.update("1", task, "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("Deletar tarefa retorna No Content")
    void deleteTask() {
        doNothing().when(service).delete("1", "user1");

        ResponseEntity<Void> response = controller.delete("1", "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete("1", "user1");
    }

    @Test
    @DisplayName("Toggle completed retorna tarefa atualizada")
    void toggleCompleted() {
        Task task = Task.builder().id("1").userId("user1").completed(true).build();
        when(service.toggleCompleted("1", true, "user1")).thenReturn(task);

        TaskController.ToggleRequest req = new TaskController.ToggleRequest();
        req.completed = true;
        ResponseEntity<TaskDTO> response = controller.toggleCompleted("1", req, "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCompleted()).isTrue();
    }

    @Test
    @DisplayName("MapToDTO converte Task com dueDate")
    void mapToDtoWithDueDate() {
        Task task = Task.builder()
                .id("1")
                .title("Test")
                .description("Desc")
                .dueDate(LocalDate.of(2024, 1, 1))
                .completed(true)
                .userId("user1")
                .listId("list1")
                .build();

        TaskDTO dto = controller.mapToDTO(task);

        assertThat(dto.getId()).isEqualTo("1");
        assertThat(dto.getDueDate()).isEqualTo("2024-01-01");
        assertThat(dto.getCompleted()).isTrue();
    }

    @Test
    @DisplayName("MapToDTO converte Task sem dueDate")
    void mapToDtoWithoutDueDate() {
        Task task = Task.builder().id("1").title("Test").completed(false).build();

        TaskDTO dto = controller.mapToDTO(task);

        assertThat(dto.getDueDate()).isNull();
    }
}
