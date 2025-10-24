package com.example.movie.dto.response.user;

import java.time.Instant;

public record UserDTO(
        Long id,
        String username,
        String fullName,
        String email,
        String status,
        String role,
        Boolean enabled,
        Instant createdAt
) {}
