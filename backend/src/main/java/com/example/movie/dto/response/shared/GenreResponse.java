package com.example.movie.dto.response.shared;

import lombok.Builder;

@Builder
public record GenreResponse(
        Long id,
        String name,
        String slug
) {}