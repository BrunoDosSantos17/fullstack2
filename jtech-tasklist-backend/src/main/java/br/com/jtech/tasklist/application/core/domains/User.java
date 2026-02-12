package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.dto.UserAuthDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString(exclude = "password")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private Boolean active = true;
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    /**
     * Converts domain model to entity.
     * Follows Dependency Inversion Principle (DIP) - domain doesn't depend on persistence.
     */
    public UserEntity toEntity() {
        return UserEntity.builder()
                .id(id != null ? UUID.fromString(id) : null)
                .name(name)
                .email(email)
                .password(password)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .active(active)
                .build();
    }

    /**
     * Creates domain model from entity.
     * Factory method pattern for object creation.
     */
    public static User of(UserAuthDTO entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.id().toString())
                .name(entity.name())
                .email(entity.email())
                .password(entity.password())
                .active(entity.active())
                .build();
    }

    /**
     * Creates domain models from entities list.
     */
    public static List<User> of(List<UserAuthDTO> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(User::of)
                .toList();
    }

    /**
     * Validates email format.
     * Business rule validation.
     */
    public boolean isValidEmail() {
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Checks if user is active.
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Returns user without password for security.
     * Follows Open/Closed Principle (OCP) - secure representation.
     */
    public User withoutPassword() {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .active(active)
                .build();
    }
}
