package com.example.movie.dto.response.admin;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record AdminBookingListResponse(
        Long id, String status, Integer quantity, BigDecimal totalPrice,
        String paymentMethod, String paymentTxnId,
        String customerName, String customerEmail,
        Instant createdAt, Instant paidAt,
        Long showtimeId, Instant startTime,
        Long cinemaId, String cinemaName,
        Long movieId, String movieTitle
) {}