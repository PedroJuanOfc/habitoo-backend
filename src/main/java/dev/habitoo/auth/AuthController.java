package dev.habitoo.auth;

import dev.habitoo.user.User;
import dev.habitoo.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest body) {
        if (userRepository.existsByEmail(body.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SimpleError("E-mail já em uso"));
        }

        String hash = passwordEncoder.encode(body.getPassword());

        User user = new User(body.getName(), body.getEmail(), hash);
        user = userRepository.save(user);

        RegisterResponse resp = new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body) {
        try {
            String token = authService.login(body.getEmail(), body.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthService.AuthException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleError(ex.getMessage()));
        }
    }

    static class SimpleError {
        public final String message;
        public SimpleError(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}