package com.example.movie.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleRequest(
        @NotBlank String role // ADMIN | USER
) {}
