package com.example.movie.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AdminUserRoleRequest(
        @NotBlank(message = "Role is required")
        String role // ADMIN | USER
) {}