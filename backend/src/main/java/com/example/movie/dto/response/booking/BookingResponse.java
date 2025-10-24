package com.example.movie.dto.response.booking;

import java.math.BigDecimal;

public class BookingResponse {
    public Long id;
    public String status;
    public BigDecimal unitPrice;
    public BigDecimal totalPrice;
    public long remaining;

    public BookingResponse(Long id, String status, BigDecimal unitPrice, BigDecimal totalPrice, long remaining) {
        this.id = id;
        this.status = status;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.remaining = remaining;
    }
}