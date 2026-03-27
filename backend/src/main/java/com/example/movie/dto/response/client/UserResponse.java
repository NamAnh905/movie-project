package com.example.movie.dto.response.client;

import lombok.Builder;

@Builder
public record UserResponse(
        String username,
        String fullName,
        String email,
        String status
) {}
