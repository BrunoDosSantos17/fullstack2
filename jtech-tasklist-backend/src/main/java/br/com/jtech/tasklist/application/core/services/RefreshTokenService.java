package br.com.jtech.tasklist.application.core.services;

import br.com.jtech.tasklist.application.core.domains.RefreshToken;
import br.com.jtech.tasklist.application.core.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    public RefreshToken create(String userId) {
        return repository.save(RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(UUID.fromString(userId))
                .expiresAt(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .build());
    }

    public Optional<RefreshToken> validate(String token) {
        return repository.findByTokenAndRevokedFalse(token)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()));
    }

    public void revoke(String token) {
        repository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }

}
