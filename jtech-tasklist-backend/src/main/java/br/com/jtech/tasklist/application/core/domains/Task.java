package br.com.jtech.tasklist.application.core.domains;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
@Builder
public class Task {

    private final UUID id;
    private final String title;
    private final String description;
    private final String listName;
    private boolean completed;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final UUID userId;

    private Task(UUID id,
                 String title,
                 String description,
                 String listName,
                 boolean completed,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt,
                 UUID userId) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (listName == null || listName.isBlank()) {
            throw new IllegalArgumentException("List name is required");
        }

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        this.id = id;
        this.title = title;
        this.description = description;
        this.listName = listName;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    /* ================= Factory Methods ================= */

    public static Task create(String title,
                              String description,
                              String listName,
                              UUID userId) {

        return new Task(
                null,
                title,
                description,
                listName,
                false,
                LocalDateTime.now(),
                null,
                userId
        );
    }

    public static Task restore(UUID id,
                               String title,
                               String description,
                               String listName,
                               boolean completed,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt,
                               UUID userId) {

        return new Task(
                id,
                title,
                description,
                listName,
                completed,
                createdAt,
                updatedAt,
                userId
        );
    }

    /* ================= Business Rules ================= */

    public void toggleCompleted() {
        this.completed = !this.completed;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.completed = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsNotCompleted() {
        this.completed = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean belongsToUser(UUID userId) {
        return this.userId.equals(userId);
    }

    public static List<Task> fromList(List<Task> tasks) {
        return tasks == null ? List.of() : tasks;
    }
}
