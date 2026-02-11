package br.com.jtech.tasklist.application.core.usecases.auth;


import br.com.jtech.tasklist.adapters.input.protocols.auth.AuthResponse;
import br.com.jtech.tasklist.adapters.input.protocols.auth.RegisterRequest;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import br.com.jtech.tasklist.config.infra.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered: " + request.email());
        }

        var userEntity = UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .active(true)
                .build();

        var savedUser = userRepository.save(userEntity);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate tokens
        var accessToken = tokenProvider.generateTokenFromUsername(savedUser.getEmail());
        var refreshToken = tokenProvider.generateRefreshToken(savedUser.getEmail());

        // Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(savedUser.getId().toString())
                        .name(savedUser.getName())
                        .email(savedUser.getEmail())
                        .build())
                .build();
    }
}
