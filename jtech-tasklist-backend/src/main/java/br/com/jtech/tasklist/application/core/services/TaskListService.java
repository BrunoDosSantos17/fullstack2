package br.com.jtech.tasklist.application.core.services;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListRepositoryPort;
import br.com.jtech.tasklist.application.ports.output.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskListService {

    private final TaskListRepositoryPort repository;
    private final TaskRepositoryPort taskRepository;

    public TaskList create(TaskList taskList) {
        return repository.save(taskList);
    }

    public Optional<TaskList> getById(String id, String userId) {
        var taskList = repository.findById(id);
        if (taskList.isPresent() && !taskList.get().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para acessar esta lista.");
        }
        return taskList;
    }

    public List<TaskList> getByUser(String userId) {
        return repository.findAllByUserId(userId);
    }

    public void delete(String id, String userId) {
        var taskList = repository.findById(id);
        if (taskList.isEmpty()) {
            throw new IllegalArgumentException("Lista não encontrada.");
        }
        if (!taskList.get().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para excluir esta lista.");
        }
        if (taskRepository.existsByListId(id)) {
            throw new IllegalArgumentException("Existem tarefas vinculadas a esta lista. Exclua as tarefas antes de excluir a lista.");
        }
        repository.deleteById(id);
    }

    public List<TaskList> getAll() {
        return repository.findAll();
    }

    public TaskList update(TaskList taskList, String userId) {
        var existing = repository.findById(taskList.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Lista não encontrada.");
        }
        if (!existing.get().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Você não tem permissão para atualizar esta lista.");
        }
        return repository.update(taskList);
    }
}
