package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.dtos.LoginDto;
import br.com.jtech.tasklist.adapters.input.dtos.UserDto;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.core.services.RefreshTokenService;
import br.com.jtech.tasklist.application.core.services.UserService;
import br.com.jtech.tasklist.config.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<User> create(@RequestBody UserDto user) {
        return ResponseEntity.ok(service.create(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto request) {
        var email = request.email();
        var password = request.password();
        var user = service.login(email, password);

        if (user != null) {
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            var refreshToken = refreshTokenService.create(user.getId());
            return ResponseEntity.ok(Map.of(
                    "user", Map.of(
                            "id", user.getId(),
                            "name", user.getName(),
                            "email", user.getEmail()
                    ),
                    "token", token,
                    "refreshToken", refreshToken.getToken()
            ));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        var opt = refreshTokenService.validate(refreshToken);
        if (opt.isPresent()) {
            var rt = opt.get();
            var user = service.getById(rt.getUserId().toString()).orElse(null);
            if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid user"));
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            return ResponseEntity.ok(Map.of(
                    "token", token
            ));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null) {
            refreshTokenService.revoke(refreshToken);
        }
        return ResponseEntity.ok().build();
    }
}
