package com.example.movie.dto.response.revenue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RevenueDTOs {
    public record Summary(BigDecimal revenue, long tickets, long bookings) {}
    public record Point(LocalDate period, BigDecimal revenue, long tickets, long bookings) {}
    public record ByCinema(long cinemaId, String cinemaName, BigDecimal revenue, long tickets, long bookings) {}
    public record ByMovie(long movieId, String movieTitle, BigDecimal revenue, long tickets, long bookings) {}
    public record Overview(Summary summary, List<Point> series, List<ByCinema> byCinema, List<ByMovie> byMovie) {}
}