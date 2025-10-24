package com.example.movie.dto.response.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowtimeDTO(
        Long id,
        Long movieId,
        String movieTitle,
        Long cinemaId,
        String cinemaName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal price,
        String status,         // OPEN/CLOSED...
        String computedState   // NOW_SHOWING | UPCOMING | ENDED
) {}
