package br.com.jtech.tasklist.application.core.services;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepositoryPort repository;

    @InjectMocks
    private TaskService service;

    @Test
    @DisplayName("Criar tarefa com título único retorna tarefa salva")
    void createTaskSuccess() {
        Task task = Task.builder().title("Task").listId("list1").userId("user1").completed(false).build();
        when(repository.existsByTitleAndListId("Task", "list1")).thenReturn(false);
        when(repository.save(task)).thenReturn(task);

        Task result = service.create(task);

        assertThat(result).isEqualTo(task);
        verify(repository).save(task);
    }

    @Test
    @DisplayName("Criar tarefa com título duplicado lança exceção")
    void createTaskDuplicate() {
        Task task = Task.builder().title("Task").listId("list1").build();
        when(repository.existsByTitleAndListId("Task", "list1")).thenReturn(true);

        assertThatThrownBy(() -> service.create(task))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Já existe uma tarefa com esse título nesta lista.");
    }

    @Test
    @DisplayName("Buscar tarefa por ID com usuário correto retorna tarefa")
    void getByIdAuthorized() {
        Task task = Task.builder().id("1").userId("user1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(task));

        Optional<Task> result = service.getById("1", "user1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1");
    }

    @Test
    @DisplayName("Buscar tarefa por ID com usuário incorreto lança exceção")
    void getByIdUnauthorized() {
        Task task = Task.builder().id("1").userId("user1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.getById("1", "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não tem permissão para acessar esta tarefa.");
    }

    @Test
    @DisplayName("Buscar tarefa inexistente retorna vazio")
    void getByIdNotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        Optional<Task> result = service.getById("999", "user1");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Buscar tarefas por usuário retorna lista")
    void getByUser() {
        List<Task> tasks = List.of(Task.builder().id("1").userId("user1").completed(false).build());
        when(repository.findAllByUserId("user1")).thenReturn(tasks);

        List<Task> result = service.getByUser("user1");

        assertThat(result).hasSize(1);
        verify(repository).findAllByUserId("user1");
    }

    @Test
    @DisplayName("Buscar tarefas por lista filtra por usuário")
    void getByList() {
        List<Task> tasks = List.of(
                Task.builder().id("1").listId("list1").userId("user1").completed(false).build(),
                Task.builder().id("2").listId("list1").userId("user2").completed(false).build()
        );
        when(repository.findAllByListId("list1")).thenReturn(tasks);

        List<Task> result = service.getByList("list1", "user1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user1");
    }

    @Test
    @DisplayName("Buscar todas as tarefas retorna todas")
    void getAll() {
        List<Task> tasks = List.of(Task.builder().id("1").completed(false).build());
        when(repository.findAll()).thenReturn(tasks);

        List<Task> result = service.getAll();

        assertThat(result).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deletar tarefa autorizada")
    void deleteAuthorized() {
        Task task = Task.builder().id("1").userId("user1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(task));

        service.delete("1", "user1");

        verify(repository).deleteById("1");
    }

    @Test
    @DisplayName("Deletar tarefa inexistente lança exceção")
    void deleteNotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("999", "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tarefa não encontrada.");
    }

    @Test
    @DisplayName("Deletar tarefa não autorizada lança exceção")
    void deleteUnauthorized() {
        Task task = Task.builder().id("1").userId("user1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.delete("1", "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não tem permissão para excluir esta tarefa.");
    }

    @Test
    @DisplayName("Atualizar tarefa autorizada retorna tarefa atualizada")
    void updateAuthorized() {
        Task existing = Task.builder().id("1").userId("user1").title("Old").listId("list1").completed(false).build();
        Task updated = Task.builder().id("1").userId("user1").title("New").listId("list1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.existsByTitleAndListId("New", "list1")).thenReturn(false);
        when(repository.update(updated)).thenReturn(updated);

        Task result = service.update(updated, "user1");

        assertThat(result.getTitle()).isEqualTo("New");
        verify(repository).update(updated);
    }

    @Test
    @DisplayName("Atualizar tarefa inexistente lança exceção")
    void updateNotFound() {
        Task task = Task.builder().id("999").completed(false).build();
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(task, "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tarefa não encontrada.");
    }

    @Test
    @DisplayName("Atualizar tarefa não autorizada lança exceção")
    void updateUnauthorized() {
        Task existing = Task.builder().id("1").userId("user1").completed(false).build();
        Task updated = Task.builder().id("1").userId("user1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(updated, "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não tem permissão para atualizar esta tarefa.");
    }

    @Test
    @DisplayName("Atualizar tarefa com título duplicado lança exceção")
    void updateDuplicateTitle() {
        Task existing = Task.builder().id("1").userId("user1").title("Old").listId("list1").completed(false).build();
        Task updated = Task.builder().id("1").userId("user1").title("New").listId("list1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.existsByTitleAndListId("New", "list1")).thenReturn(true);

        assertThatThrownBy(() -> service.update(updated, "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Já existe uma tarefa com esse título nesta lista.");
    }

    @Test
    @DisplayName("Atualizar tarefa mantendo mesmo título não verifica duplicação")
    void updateSameTitle() {
        Task existing = Task.builder().id("1").userId("user1").title("Same").listId("list1").completed(false).build();
        Task updated = Task.builder().id("1").userId("user1").title("Same").listId("list1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.update(updated)).thenReturn(updated);

        Task result = service.update(updated, "user1");

        assertThat(result).isEqualTo(updated);
        verify(repository, never()).existsByTitleAndListId(anyString(), anyString());
    }

    @Test
    @DisplayName("Toggle completed autorizado retorna tarefa atualizada")
    void toggleCompletedAuthorized() {
        Task task = Task.builder().id("1").userId("user1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(task));
        when(repository.update(task)).thenReturn(task);

        Task result = service.toggleCompleted("1", true, "user1");

        assertThat(result.isCompleted()).isTrue();
        verify(repository).update(task);
    }

    @Test
    @DisplayName("Toggle completed tarefa inexistente lança exceção")
    void toggleCompletedNotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggleCompleted("999", true, "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tarefa não encontrada.");
    }

    @Test
    @DisplayName("Toggle completed não autorizado lança exceção")
    void toggleCompletedUnauthorized() {
        Task task = Task.builder().id("1").userId("user1").completed(false).build();
        when(repository.findById("1")).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.toggleCompleted("1", true, "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não tem permissão para alterar esta tarefa.");
    }
}
