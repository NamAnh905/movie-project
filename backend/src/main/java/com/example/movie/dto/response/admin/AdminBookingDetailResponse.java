package com.example.movie.dto.response.admin;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record AdminBookingDetailResponse(
        Long id, Long userId, Long showtimeId,
        String customerName, String customerEmail,
        Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice,
        String status, String paymentMethod, String paymentTxnId,
        Instant createdAt, Instant paidAt,
        Instant startTime, Long cinemaId, String cinemaName,
        Long movieId, String movieTitle, BigDecimal showtimePrice,
        List<BookingEvent> timeline
) {
    @Builder
    public record BookingEvent(String type, Instant at, String note) {}
}