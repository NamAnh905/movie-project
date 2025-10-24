package com.example.movie.dto.response.booking;

import com.example.movie.model.booking.Booking;
import com.example.movie.model.cinema.Showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingDetailResponse {
    public Long id;
    public String status;
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal totalPrice;
    public String customerName;
    public String customerEmail;

    // Thêm cho FE
    public String movieTitle;
    public String cinemaName;
    public LocalDateTime startTime;

    public static BookingDetailResponse from(Booking b) {
        BookingDetailResponse r = new BookingDetailResponse();
        r.id = b.getId();
        r.status = b.getStatus();
        r.quantity = b.getQuantity();
        r.unitPrice = b.getUnitPrice();
        r.totalPrice = b.getTotalPrice();
        r.customerName = b.getCustomerName();
        r.customerEmail = b.getCustomerEmail();

        Showtime s = b.getShowtime();
        if (s != null) {
            r.startTime = s.getStartTime();
            if (s.getMovie() != null)  r.movieTitle  = s.getMovie().getTitle();
            if (s.getCinema() != null) r.cinemaName = s.getCinema().getName();
        }
        return r;
    }
}
