package com.example.movie.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AdminGenreRequest(
        @NotBlank(message = "Tên thể loại không được để trống") String name,
        String slug
) {}