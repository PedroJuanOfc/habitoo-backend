package dev.habitoo.auth;

import dev.habitoo.user.User;
import dev.habitoo.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Credenciais inválidas"));

        boolean matches = passwordEncoder.matches(password, user.getPasswordHash());
        if (!matches) {
            throw new AuthException("Credenciais inválidas");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public static class AuthException extends RuntimeException {
        public AuthException(String message) { super(message); }
    }
}