package com.example.movie.dto.request.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VnpayPaymentRequest(
        @NotNull(message = "Thiếu ID suất chiếu.")
        Long showtimeId,
        @NotNull(message = "Thiếu số lượng vé.")
        @Min(1)
        Integer quantity,
        String coupon,
        String customerName,
        String customerEmail,
        @NotBlank(message = "Thiếu URL chuyển hướng.")
        String clientReturnUrl
) {}