package com.example.movie.service.user;

import com.example.movie.model.user.User;
import com.example.movie.repository.user.UserRepository;
import com.example.movie.dto.request.auth.AuthRequest;
import com.example.movie.dto.request.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Đăng ký nhanh từ username/password */
    public User create(AuthRequest req) {
        if (userRepository.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        User u = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .role("USER")
                .build();
        return userRepository.save(u);
    }

    public User register(RegisterRequest dto) {
        if (userRepository.existsByUsernameIgnoreCase(dto.username())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        String hash = passwordEncoder.encode(dto.password());

        User u = new User();
        u.setUsername(dto.username());
        u.setPassword(hash);          // dùng chung hash
        u.setPasswordHash(hash);      // DB bắt buộc NOT NULL
        u.setEmail(dto.email());
        u.setFullName(dto.fullName());
        u.setRole("USER");
        u.setStatus("ACTIVE");
        u.setEnabled(true);
        try {
            return userRepository.save(u);
        } catch (DataIntegrityViolationException e) {
            // Phòng trường hợp race condition: DB vẫn là “chốt chặn” cuối cùng
            throw e;
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
