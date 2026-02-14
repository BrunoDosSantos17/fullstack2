package br.com.jtech.tasklist.application.core.repositories;

import br.com.jtech.tasklist.application.core.domains.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(UUID userId);
}
