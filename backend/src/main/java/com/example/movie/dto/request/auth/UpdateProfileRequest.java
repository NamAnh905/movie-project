package com.example.movie.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 255) String fullName,
        @Email @Size(max = 255) String email
) {}
