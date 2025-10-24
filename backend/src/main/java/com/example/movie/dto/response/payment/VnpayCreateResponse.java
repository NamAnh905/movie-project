package com.example.movie.dto.response.payment;

public class VnpayCreateResponse {
    public Long bookingId;
    public String paymentUrl;
    public VnpayCreateResponse(Long id, String url){ this.bookingId=id; this.paymentUrl=url; }
}
