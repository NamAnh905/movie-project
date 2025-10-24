package com.example.movie.dto.request.payment;

public class VnpayCreateRequest {
    public Long showtimeId;
    public Integer quantity;
    public String coupon;          // optional
    public String customerName;    // guest
    public String customerEmail;   // guest
    public String clientReturnUrl; // FE page to redirect after BE processes result
}