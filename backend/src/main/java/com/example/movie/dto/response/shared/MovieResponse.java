package com.example.movie.dto.response.shared;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record MovieResponse(
        Long id,
        String title,
        String description,
        Integer duration,
        LocalDate releaseDate,
        String language,
        String country,
        String status,
        String ageRating,
        String posterUrl,
        Integer year,
        List<Long> genreIds,
        List<String> genreNames
) {}