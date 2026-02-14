package br.com.jtech.tasklist.application.core.domains;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_lists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskList {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "name")
    private String name;
}