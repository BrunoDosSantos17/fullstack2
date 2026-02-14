package br.com.jtech.tasklist.application.core.services;

import br.com.jtech.tasklist.adapters.input.dtos.UserDto;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.UserRepositoryPort;
import br.com.jtech.tasklist.config.infra.exceptions.BadCredentialsException;
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryPort repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User create(UserDto user) {
        if (user.email() == null || !user.email().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BusinessException("Email inválido.");
        }
        if (repository.findByEmail(user.email()).isPresent()) {
            throw new BusinessException("Já existe um usuário cadastrado com este email.");
        }

        return repository.save(user);
    }

    public Optional<User> getById(String id) {
        return repository.findById(id);
    }

    public User login(String email, String password) {
        var user = repository.findByEmail(email).orElseThrow(() ->
                        new BadCredentialsException("Email ou senha inválidas"));

        if (!passwordEncoder.matches(password, user.getPassword()) && !password.equals(user.getPassword())) {
            throw new BadCredentialsException("Email ou senha inválidas");
        }

        return user;
    }
}
