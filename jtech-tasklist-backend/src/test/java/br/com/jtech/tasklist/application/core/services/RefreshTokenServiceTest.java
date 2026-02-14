package br.com.jtech.tasklist.application.core.services;

import br.com.jtech.tasklist.application.core.domains.RefreshToken;
import br.com.jtech.tasklist.application.core.repositories.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    @Test
    @DisplayName("Criar refresh token retorna token válido")
    void createRefreshToken() {
        ReflectionTestUtils.setField(service, "refreshExpiration", 604800000L);
        String userId = UUID.randomUUID().toString();
        RefreshToken savedToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token("token123")
                .userId(UUID.fromString(userId))
                .expiresAt(Instant.now().plusMillis(604800000L))
                .revoked(false)
                .build();

        when(repository.save(any(RefreshToken.class))).thenReturn(savedToken);

        RefreshToken result = service.create(userId);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("token123");
        assertThat(result.isRevoked()).isFalse();
        verify(repository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Validar token válido e não revogado retorna token")
    void validateValidToken() {
        RefreshToken token = RefreshToken.builder()
                .token("valid123")
                .expiresAt(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(repository.findByTokenAndRevokedFalse("valid123")).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = service.validate("valid123");

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("valid123");
    }

    @Test
    @DisplayName("Validar token expirado retorna vazio")
    void validateExpiredToken() {
        RefreshToken token = RefreshToken.builder()
                .token("expired123")
                .expiresAt(Instant.now().minusSeconds(3600))
                .revoked(false)
                .build();

        when(repository.findByTokenAndRevokedFalse("expired123")).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = service.validate("expired123");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Validar token revogado retorna vazio")
    void validateRevokedToken() {
        when(repository.findByTokenAndRevokedFalse("revoked123")).thenReturn(Optional.empty());

        Optional<RefreshToken> result = service.validate("revoked123");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Validar token inexistente retorna vazio")
    void validateNonExistentToken() {
        when(repository.findByTokenAndRevokedFalse("nonexistent")).thenReturn(Optional.empty());

        Optional<RefreshToken> result = service.validate("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Revogar token existente marca como revogado")
    void revokeExistingToken() {
        RefreshToken token = RefreshToken.builder()
                .token("token123")
                .revoked(false)
                .build();

        when(repository.findByToken("token123")).thenReturn(Optional.of(token));
        when(repository.save(any(RefreshToken.class))).thenReturn(token);

        service.revoke("token123");

        assertThat(token.isRevoked()).isTrue();
        verify(repository).save(token);
    }

    @Test
    @DisplayName("Revogar token inexistente não faz nada")
    void revokeNonExistentToken() {
        when(repository.findByToken("nonexistent")).thenReturn(Optional.empty());

        service.revoke("nonexistent");

        verify(repository, never()).save(any());
    }
}
