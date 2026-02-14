package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.dtos.LoginDto;
import br.com.jtech.tasklist.adapters.input.dtos.UserDto;
import br.com.jtech.tasklist.application.core.domains.RefreshToken;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.core.services.RefreshTokenService;
import br.com.jtech.tasklist.application.core.services.UserService;
import br.com.jtech.tasklist.config.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserService service;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserController controller;

    @Test
    @DisplayName("Registrar usuário retorna OK")
    void registerUser() {
        UserDto dto = new UserDto("Test", "test@test.com", "123");
        User user = User.builder().id("1").name("Test").email("test@test.com").build();
        when(service.create(dto)).thenReturn(user);

        ResponseEntity<User> response = controller.create(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo("1");
        verify(service).create(dto);
    }

    @Test
    @DisplayName("Buscar usuário por ID existente retorna OK")
    void getByIdFound() {
        User user = User.builder().id("1").name("Test").email("test@test.com").build();
        when(service.getById("1")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = controller.getById("1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("Buscar usuário por ID inexistente retorna Not Found")
    void getByIdNotFound() {
        when(service.getById("999")).thenReturn(Optional.empty());

        ResponseEntity<User> response = controller.getById("999");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Login com credenciais válidas retorna token")
    void loginSuccess() {
        LoginDto loginDto = new LoginDto("test@test.com", "123");
        User user = User.builder().id("1").name("Test").email("test@test.com").build();
        RefreshToken refreshToken = RefreshToken.builder().token("refresh123").build();
        
        when(service.login("test@test.com", "123")).thenReturn(user);
        when(jwtUtil.generateToken("1", "test@test.com")).thenReturn("token123");
        when(refreshTokenService.create("1")).thenReturn(refreshToken);

        ResponseEntity<?> response = controller.login(loginDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsKeys("user", "token", "refreshToken");
        assertThat(body.get("token")).isEqualTo("token123");
    }

    @Test
    @DisplayName("Login com credenciais inválidas retorna Unauthorized")
    void loginFailed() {
        LoginDto loginDto = new LoginDto("test@test.com", "wrong");
        when(service.login("test@test.com", "wrong")).thenReturn(null);

        ResponseEntity<?> response = controller.login(loginDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Invalid credentials");
    }

    @Test
    @DisplayName("Refresh token válido retorna novo token")
    void refreshTokenSuccess() {
        Map<String, String> request = Map.of("refreshToken", "refresh123");
        RefreshToken refreshToken = RefreshToken.builder().userId(UUID.randomUUID()).build();
        User user = User.builder().id("1").email("test@test.com").build();
        
        when(refreshTokenService.validate("refresh123")).thenReturn(Optional.of(refreshToken));
        when(service.getById(refreshToken.getUserId().toString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("1", "test@test.com")).thenReturn("newToken123");

        ResponseEntity<?> response = controller.refreshToken(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("token")).isEqualTo("newToken123");
    }

    @Test
    @DisplayName("Refresh token inválido retorna Unauthorized")
    void refreshTokenInvalid() {
        Map<String, String> request = Map.of("refreshToken", "invalid");
        when(refreshTokenService.validate("invalid")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.refreshToken(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Invalid refresh token");
    }

    @Test
    @DisplayName("Refresh token com usuário inexistente retorna Unauthorized")
    void refreshTokenUserNotFound() {
        Map<String, String> request = Map.of("refreshToken", "refresh123");
        RefreshToken refreshToken = RefreshToken.builder().userId(UUID.randomUUID()).build();
        
        when(refreshTokenService.validate("refresh123")).thenReturn(Optional.of(refreshToken));
        when(service.getById(refreshToken.getUserId().toString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.refreshToken(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertThat(body.get("error")).isEqualTo("Invalid user");
    }

    @Test
    @DisplayName("Logout revoga refresh token e retorna OK")
    void logoutSuccess() {
        Map<String, String> request = Map.of("refreshToken", "refresh123");
        doNothing().when(refreshTokenService).revoke("refresh123");

        ResponseEntity<?> response = controller.logout(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(refreshTokenService).revoke("refresh123");
    }

    @Test
    @DisplayName("Logout sem refresh token retorna OK")
    void logoutWithoutToken() {
        Map<String, String> request = Map.of();

        ResponseEntity<?> response = controller.logout(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(refreshTokenService, never()).revoke(any());
    }
}
