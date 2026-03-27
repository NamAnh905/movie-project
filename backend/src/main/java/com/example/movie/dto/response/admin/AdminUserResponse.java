package com.example.movie.dto.response.admin;

import lombok.Builder;
import java.time.Instant;

@Builder
public record AdminUserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String status,
        String role,
        Boolean enabled,
        Instant createdAt
) {}