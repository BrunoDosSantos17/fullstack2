package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.TaskList;

import java.util.List;
import java.util.Optional;

public interface TaskListRepositoryPort {
    TaskList save(TaskList taskList);
    TaskList update(TaskList taskList);
    Optional<TaskList> findById(String id);
    List<TaskList> findAll();
    List<TaskList> findAllByUserId(String userId);
    void deleteById(String id);
}
