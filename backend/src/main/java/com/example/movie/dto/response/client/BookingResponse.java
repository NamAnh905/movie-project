package com.example.movie.dto.response.client;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    Long id;
    String status;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
    String customerName;
    String customerEmail;

    String movieTitle;
    String cinemaName;
    LocalDateTime startTime;

    // Dùng trả về lúc vừa tạo đơn xong để biết còn bao nhiêu ghế
    Integer remainingSeats;
}