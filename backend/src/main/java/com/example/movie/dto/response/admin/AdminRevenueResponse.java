package com.example.movie.dto.response.admin;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record AdminRevenueResponse(
        SummaryResponse summary,
        List<PointResponse> series,
        List<ByCinemaResponse> byCinema,
        List<ByMovieResponse> byMovie
) {
    @Builder public record SummaryResponse(BigDecimal revenue, Long tickets, Long bookings) {}
    @Builder public record PointResponse(LocalDate date, BigDecimal revenue, Long tickets, Long bookings) {}
    @Builder public record ByCinemaResponse(Long cinemaId, String cinemaName, BigDecimal revenue, Long tickets, Long bookings) {}
    @Builder public record ByMovieResponse(Long movieId, String movieTitle, BigDecimal revenue, Long tickets, Long bookings) {}
}