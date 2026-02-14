package br.com.jtech.tasklist.adapters.input.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private String id;

    private String title;

    private String description;

    private String dueDate;

    private Boolean completed;

    private String userId;

    private String listId;
}
