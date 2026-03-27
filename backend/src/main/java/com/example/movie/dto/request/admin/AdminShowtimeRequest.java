package com.example.movie.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record AdminShowtimeRequest(
        @NotNull
        Long movieId,
        @NotNull
        Long cinemaId,
        @NotBlank
        String date,
        @NotEmpty
        List<String> times,
        BigDecimal price,
        Integer capacity
) {}