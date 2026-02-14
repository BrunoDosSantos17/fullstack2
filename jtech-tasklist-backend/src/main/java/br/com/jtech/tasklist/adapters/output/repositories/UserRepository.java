package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.input.dtos.UserDto;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    @Override
    public User save(UserDto user) {
        var entity = UserEntity.builder()
                .name(user.name())
                .email(user.email())
                .password(user.password())
                .build();

        var saved = repository.save(entity);
        return User.builder()
                .id(saved.getId().toString())
                .name(saved.getName())
                .email(saved.getEmail())
                .password(saved.getPassword())
                .build();
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(java.util.UUID.fromString(id))
                .map(e -> User.builder()
                        .id(e.getId().toString())
                        .name(e.getName())
                        .email(e.getEmail())
                        .password(e.getPassword())
                        .build());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(e -> User.builder()
                        .id(e.getId().toString())
                        .name(e.getName())
                        .email(e.getEmail())
                        .password(e.getPassword())
                        .build());
    }

}
