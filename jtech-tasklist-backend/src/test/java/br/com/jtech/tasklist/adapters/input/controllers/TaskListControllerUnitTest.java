package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.dtos.TaskListDTO;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.core.services.TaskListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskListControllerUnitTest {

    @Mock
    private TaskListService service;

    @InjectMocks
    private TaskListController controller;

    @Test
    @DisplayName("Criar lista retorna OK")
    void createTaskList() {
        TaskList taskList = TaskList.builder().id("1").name("Lista").userId("user1").build();
        when(service.create(any(TaskList.class))).thenReturn(taskList);

        ResponseEntity<TaskListDTO> response = controller.create(taskList);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo("1");
        verify(service).create(taskList);
    }

    @Test
    @DisplayName("Buscar lista por ID existente retorna OK")
    void getByIdFound() {
        TaskList taskList = TaskList.builder().id("1").name("Lista").userId("user1").build();
        when(service.getById("1", "user1")).thenReturn(Optional.of(taskList));

        ResponseEntity<TaskListDTO> response = controller.getById("1", "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Lista");
    }

    @Test
    @DisplayName("Buscar lista por ID inexistente retorna Not Found")
    void getByIdNotFound() {
        when(service.getById("999", "user1")).thenReturn(Optional.empty());

        ResponseEntity<TaskListDTO> response = controller.getById("999", "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Buscar listas por usuário retorna lista")
    void getByUser() {
        List<TaskList> lists = List.of(TaskList.builder().id("1").name("Lista").userId("user1").build());
        when(service.getByUser("user1")).thenReturn(lists);

        ResponseEntity<List<TaskListDTO>> response = controller.getByUser("user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Buscar todas as listas retorna listas do usuário")
    void getAll() {
        List<TaskList> lists = List.of(TaskList.builder().id("1").name("Lista").userId("user1").build());
        when(service.getByUser("user1")).thenReturn(lists);

        ResponseEntity<List<TaskListDTO>> response = controller.getAll("user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Atualizar lista retorna lista atualizada")
    void updateTaskList() {
        TaskList taskList = TaskList.builder().id("1").name("Updated").userId("user1").build();
        when(service.update(any(TaskList.class), eq("user1"))).thenReturn(taskList);

        ResponseEntity<TaskListDTO> response = controller.update("1", taskList, "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("Deletar lista retorna No Content")
    void deleteTaskList() {
        doNothing().when(service).delete("1", "user1");

        ResponseEntity<Void> response = controller.delete("1", "user1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).delete("1", "user1");
    }

    @Test
    @DisplayName("MapToDTO converte TaskList corretamente")
    void mapToDto() {
        TaskList taskList = TaskList.builder().id("1").name("Lista").userId("user1").build();

        TaskListDTO dto = controller.mapToDTO(taskList);

        assertThat(dto.getId()).isEqualTo("1");
        assertThat(dto.getName()).isEqualTo("Lista");
        assertThat(dto.getUserId()).isEqualTo("user1");
    }
}
