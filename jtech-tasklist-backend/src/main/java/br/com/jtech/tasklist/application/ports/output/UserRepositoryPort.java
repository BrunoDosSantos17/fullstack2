package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.adapters.input.dtos.UserDto;
import br.com.jtech.tasklist.application.core.domains.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(UserDto user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
}
