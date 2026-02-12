package br.com.jtech.tasklist.application.core.usecases.auth;


import br.com.jtech.tasklist.adapters.input.protocols.auth.AuthResponse;
import br.com.jtech.tasklist.adapters.input.protocols.auth.LoginRequest;
import br.com.jtech.tasklist.adapters.input.protocols.auth.RegisterRequest;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.config.infra.exceptions.BusinessException;
import br.com.jtech.tasklist.config.infra.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

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

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Authenticating user with email: {}", request.email());

        // Authenticate user
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        var user = userRepository.findActiveByEmail(request.email())
                .orElseThrow(() -> new BusinessException("User not found or inactive"));

        // Generate tokens
        var accessToken = tokenProvider.generateToken(authentication);
        var refreshToken = tokenProvider.generateRefreshToken(user.email());

        log.info("User authenticated successfully: {}", user.email());

        // Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.id().toString())
                        .name(user.name())
                        .email(user.email())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");

        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }

        // Extract username from token
        var email = tokenProvider.getUsernameFromToken(refreshToken);

        // Get user
        var user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found or inactive"));

        // Generate new tokens
        var newAccessToken = tokenProvider.generateTokenFromUsername(user.email());
        var newRefreshToken = tokenProvider.generateRefreshToken(user.email());

        log.info("Access token refreshed successfully for user: {}", user.email());

        // Build response
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(user.id().toString())
                        .name(user.name())
                        .email(user.email())
                        .build())
                .build();
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }

        String email = authentication.getName();
        var user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        return User.of(user).withoutPassword();
    }
}
