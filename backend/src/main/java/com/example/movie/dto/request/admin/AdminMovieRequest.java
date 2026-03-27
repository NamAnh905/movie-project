package com.example.movie.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record AdminMovieRequest(

        @NotBlank(message = "Tên phim không được để trống")
        String title,

        Integer year,
        String description,
        Integer duration,
        LocalDate releaseDate,
        String language,
        String country,

        @NotBlank(message = "Trạng thái không được để trống")
        String status,

        String ageRating,
        String posterUrl,
        List<Long> genreIds
) {}
