package com.example.movie.dto.request.client;

import lombok.Builder;

@Builder
public record UserProfileRequest(
        String fullName,
        String email
) {}
