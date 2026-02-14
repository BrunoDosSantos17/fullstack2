package br.com.jtech.tasklist.application.core.services;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListRepositoryPort;
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
class TaskListServiceTest {

    @Mock
    private TaskListRepositoryPort repository;

    @Mock
    private TaskRepositoryPort taskRepository;

    @InjectMocks
    private TaskListService service;

    @Test
    @DisplayName("Criar lista retorna lista salva")
    void createTaskList() {
        TaskList taskList = TaskList.builder().name("Lista").userId("user1").build();
        when(repository.save(taskList)).thenReturn(taskList);

        TaskList result = service.create(taskList);

        assertThat(result).isEqualTo(taskList);
        verify(repository).save(taskList);
    }

    @Test
    @DisplayName("Buscar lista por ID com usuário correto retorna lista")
    void getByIdAuthorized() {
        TaskList taskList = TaskList.builder().id("1").userId("user1").build();
        when(repository.findById("1")).thenReturn(Optional.of(taskList));

        Optional<TaskList> result = service.getById("1", "user1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1");
    }

    @Test
    @DisplayName("Buscar lista por ID com usuário incorreto lança exceção")
    void getByIdUnauthorized() {
        TaskList taskList = TaskList.builder().id("1").userId("user1").build();
        when(repository.findById("1")).thenReturn(Optional.of(taskList));

        assertThatThrownBy(() -> service.getById("1", "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não tem permissão para acessar esta lista.");
    }

    @Test
    @DisplayName("Buscar lista inexistente retorna vazio")
    void getByIdNotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        Optional<TaskList> result = service.getById("999", "user1");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Buscar listas por usuário retorna lista")
    void getByUser() {
        List<TaskList> lists = List.of(TaskList.builder().id("1").userId("user1").build());
        when(repository.findAllByUserId("user1")).thenReturn(lists);

        List<TaskList> result = service.getByUser("user1");

        assertThat(result).hasSize(1);
        verify(repository).findAllByUserId("user1");
    }

    @Test
    @DisplayName("Buscar todas as listas retorna todas")
    void getAll() {
        List<TaskList> lists = List.of(TaskList.builder().id("1").build());
        when(repository.findAll()).thenReturn(lists);

        List<TaskList> result = service.getAll();

        assertThat(result).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deletar lista autorizada sem tarefas vinculadas")
    void deleteAuthorized() {
        TaskList taskList = TaskList.builder().id("1").userId("user1").build();
        when(repository.findById("1")).thenReturn(Optional.of(taskList));
        when(taskRepository.existsByListId("1")).thenReturn(false);

        service.delete("1", "user1");

        verify(repository).deleteById("1");
    }

    @Test
    @DisplayName("Deletar lista inexistente lança exceção")
    void deleteNotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("999", "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lista não encontrada.");
    }

    @Test
    @DisplayName("Deletar lista não autorizada lança exceção")
    void deleteUnauthorized() {
        TaskList taskList = TaskList.builder().id("1").userId("user1").build();
        when(repository.findById("1")).thenReturn(Optional.of(taskList));

        assertThatThrownBy(() -> service.delete("1", "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não tem permissão para excluir esta lista.");
    }

    @Test
    @DisplayName("Deletar lista com tarefas vinculadas lança exceção")
    void deleteWithTasks() {
        TaskList taskList = TaskList.builder().id("1").userId("user1").build();
        when(repository.findById("1")).thenReturn(Optional.of(taskList));
        when(taskRepository.existsByListId("1")).thenReturn(true);

        assertThatThrownBy(() -> service.delete("1", "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Existem tarefas vinculadas a esta lista. Exclua as tarefas antes de excluir a lista.");
    }

    @Test
    @DisplayName("Atualizar lista autorizada retorna lista atualizada")
    void updateAuthorized() {
        TaskList existing = TaskList.builder().id("1").userId("user1").name("Old").build();
        TaskList updated = TaskList.builder().id("1").userId("user1").name("New").build();
        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.update(updated)).thenReturn(updated);

        TaskList result = service.update(updated, "user1");

        assertThat(result.getName()).isEqualTo("New");
        verify(repository).update(updated);
    }

    @Test
    @DisplayName("Atualizar lista inexistente lança exceção")
    void updateNotFound() {
        TaskList taskList = TaskList.builder().id("999").build();
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(taskList, "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lista não encontrada.");
    }

    @Test
    @DisplayName("Atualizar lista não autorizada lança exceção")
    void updateUnauthorized() {
        TaskList existing = TaskList.builder().id("1").userId("user1").build();
        TaskList updated = TaskList.builder().id("1").userId("user1").build();
        when(repository.findById("1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(updated, "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Você não tem permissão para atualizar esta lista.");
    }
}
