package com.example.movie.dto.response.booking;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class AdminBookingDTOs {

    public record ListItem(
            long id, String status, int quantity, BigDecimal totalPrice,
            String paymentMethod, String paymentTxnId,
            String customerName, String customerEmail,
            Instant createdAt, Instant paidAt,
            long showtimeId, Instant startTime,
            long cinemaId, String cinemaName,
            long movieId, String movieTitle
    ) {}

    public record Event(String type, Instant at, String note) {}

    public record Detail(
            long id, Long userId, long showtimeId,
            String customerName, String customerEmail,
            int quantity, BigDecimal unitPrice, BigDecimal totalPrice,
            String status, String paymentMethod, String paymentTxnId,
            Instant createdAt, Instant paidAt,
            Instant startTime, long cinemaId, String cinemaName,
            long movieId, String movieTitle, BigDecimal showtimePrice,
            List<Event> timeline
    ) {}

    public record PageResponse<T>(List<T> items, int page, int size, long total, int totalPages) {}
}
