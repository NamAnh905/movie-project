package com.example.movie.dto.response.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Dòng hiển thị trong lịch sử đặt vé
public record BookingHistoryItemDTO(
        Long id,
        String movieTitle,
        String cinemaName,
        LocalDateTime startTime,
        Integer quantity,
        BigDecimal totalPrice,
        String status
) {}