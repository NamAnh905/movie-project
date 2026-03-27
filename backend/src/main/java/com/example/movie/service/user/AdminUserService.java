package com.example.movie.service.user;

import com.example.movie.dto.response.admin.AdminUserResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.Role;
import com.example.movie.entity.User;
import com.example.movie.mapper.user.AdminUserMapper;
import com.example.movie.repository.RoleRepository;
import com.example.movie.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    AdminUserMapper adminUserMapper;

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> listUsers(String q, Pageable pageable) {
        Page<User> page = userRepository.searchUsers(q, pageable);
        return adminUserMapper.toPageResponse(page);
    }

    @Transactional
    public AdminUserResponse updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy Role từ Database dựa trên tên role truyền vào (VD: "ADMIN" hoặc "MANAGER")
        Role newRole = roleRepository.findById(roleName.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại trên hệ thống"));

        // Cập nhật lại danh sách Role cho User
        user.setRoles(new HashSet<>(List.of(newRole)));

        return adminUserMapper.toResponse(userRepository.save(user));
    }
}