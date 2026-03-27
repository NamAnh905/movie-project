package com.example.movie.service.user;

import com.example.movie.dto.request.client.UserProfileRequest;
import com.example.movie.dto.response.client.UserResponse;
import com.example.movie.dto.request.auth.AuthRequest;
import com.example.movie.dto.request.auth.RegisterRequest;
import com.example.movie.entity.Role;
import com.example.movie.entity.User;
import com.example.movie.mapper.user.UserMapper;
import com.example.movie.repository.RoleRepository;
import com.example.movie.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateMyProfile(String username, UserProfileRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateEntityFromRequest(req, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public User create(AuthRequest req) {
        if (userRepository.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        Role defaultRole = roleRepository.findById("USER")
                .orElseThrow(() -> new RuntimeException("Chưa khởi tạo Role USER trong Database"));

        User u = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(new HashSet<>(List.of(defaultRole)))
                .build();
        return userRepository.save(u);
    }

    @Transactional
    public User register(RegisterRequest dto) {
        if (userRepository.existsByUsernameIgnoreCase(dto.username())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmailIgnoreCase(dto.email())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // 1. Lấy Role USER mặc định từ DB
        Role defaultRole = roleRepository.findById("USER")
                .orElseThrow(() -> new RuntimeException("Chưa khởi tạo Role USER trong Database"));

        String hash = passwordEncoder.encode(dto.password());

        // 2. Build User (Xóa dòng .role("USER") cũ)
        User u = User.builder()
                .username(dto.username())
                .password(hash)
                .passwordHash(hash)
                .email(dto.email())
                .fullName(dto.fullName())
                .status("ACTIVE")
                .enabled(true)
                .roles(new HashSet<>(List.of(defaultRole))) // Gán Set<Role> mới
                .build();

        return userRepository.save(u);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}