package com.example.movie.dto.response.shared;

import lombok.Builder;

@Builder
public record VnpayPaymentResponse(
        Long bookingId,
        String paymentUrl
) {}