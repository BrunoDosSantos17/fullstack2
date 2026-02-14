package br.com.jtech.tasklist.application.core.services;

import br.com.jtech.tasklist.adapters.input.dtos.UserDto;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.UserRepositoryPort;
import br.com.jtech.tasklist.config.infra.exceptions.BadCredentialsException;
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort repository;

    @InjectMocks
    private UserService userService;

    private UserDto validUserDto;
    private User existingUser;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        validUserDto = new UserDto("John Doe", "john@example.com", "password123");
        existingUser = User.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .password("$2a$10$encoded.password.hash")
                .build();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void create_WithValidUser_ShouldReturnCreatedUser() {
        when(repository.findByEmail(validUserDto.email())).thenReturn(Optional.empty());
        when(repository.save(validUserDto)).thenReturn(existingUser);

        User result = userService.create(validUserDto);

        assertThat(result).isEqualTo(existingUser);
    }

    @Test
    void create_WithNullEmail_ShouldThrowBusinessException() {
        UserDto userWithNullEmail = new UserDto("John Doe", null, "password123");

        assertThatThrownBy(() -> userService.create(userWithNullEmail))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email inválido.");
    }

    @Test
    void create_WithInvalidEmail_ShouldThrowBusinessException() {
        UserDto userWithInvalidEmail = new UserDto("John Doe", "invalid-email", "password123");

        assertThatThrownBy(() -> userService.create(userWithInvalidEmail))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email inválido.");
    }

    @Test
    void create_WithExistingEmail_ShouldThrowBusinessException() {
        when(repository.findByEmail(validUserDto.email())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.create(validUserDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um usuário cadastrado com este email.");
    }

    @Test
    void getById_WithExistingId_ShouldReturnUser() {
        when(repository.findById("1")).thenReturn(Optional.of(existingUser));

        Optional<User> result = userService.getById("1");

        assertThat(result).isPresent().contains(existingUser);
    }

    @Test
    void getById_WithNonExistingId_ShouldReturnEmpty() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        Optional<User> result = userService.getById("999");

        assertThat(result).isEmpty();
    }

    @Test
    void login_WithValidCredentials_ShouldReturnUser() {
        String plainPassword = "password123";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        User userWithEncodedPassword = User.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .password(encodedPassword)
                .build();

        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(userWithEncodedPassword));

        User result = userService.login("john@example.com", plainPassword);

        assertThat(result).isEqualTo(userWithEncodedPassword);
    }

    @Test
    void login_WithPlainTextPassword_ShouldReturnUser() {
        String plainPassword = "password123";
        User userWithPlainPassword = User.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .password(plainPassword)
                .build();

        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(userWithPlainPassword));

        User result = userService.login("john@example.com", plainPassword);

        assertThat(result).isEqualTo(userWithPlainPassword);
    }

    @Test
    void login_WithNonExistingEmail_ShouldThrowBadCredentialsException() {
        when(repository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login("nonexistent@example.com", "password123"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email ou senha inválidas");
    }

    @Test
    void login_WithWrongPassword_ShouldThrowBadCredentialsException() {
        String encodedPassword = passwordEncoder.encode("correctPassword");
        User userWithEncodedPassword = User.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .password(encodedPassword)
                .build();

        when(repository.findByEmail("john@example.com")).thenReturn(Optional.of(userWithEncodedPassword));

        assertThatThrownBy(() -> userService.login("john@example.com", "wrongPassword"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Email ou senha inválidas");
    }
}