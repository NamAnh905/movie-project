package com.example.movie.service.booking;

import com.example.movie.dto.response.booking.AdminBookingDTOs.*;
import com.example.movie.repository.booking.AdminBookingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AdminBookingService {

    private final AdminBookingRepository repo;
    public AdminBookingService(AdminBookingRepository repo){ this.repo = repo; }

    private Date atStart(LocalDate d){ return d==null? null : Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant()); }

    public PageResponse<ListItem> search(
            LocalDate from, LocalDate to,
            Long cinemaId, Long movieId, Long showtimeId,
            String status, String paymentMethod, String q,
            int page, int size
    ){
        var p = repo.search(
                atStart(from),
                atStart(to),
                cinemaId, movieId, showtimeId, status, paymentMethod,
                (q==null || q.isBlank()) ? null : q.trim(),
                PageRequest.of(Math.max(page,0), Math.max(size,1))
        ).map(x -> new ListItem(
                x.getId(), x.getStatus(),
                x.getQuantity()==null?0:x.getQuantity(),
                x.getTotalPrice(),
                x.getPaymentMethod(), x.getPaymentTxnId(),
                x.getCustomerName(), x.getCustomerEmail(),
                x.getCreatedAt()==null?null:x.getCreatedAt().toInstant(),
                x.getPaidAt()==null?null:x.getPaidAt().toInstant(),
                x.getShowtimeId(),
                x.getStartTime()==null?null:x.getStartTime().toInstant(),
                x.getCinemaId(), x.getCinemaName(),
                x.getMovieId(), x.getMovieTitle()
        ));

        return new PageResponse<>(
                p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()
        );
    }

    public Detail getDetail(long id){
        var d = repo.findDetail(id);
        if (d == null) return null;

        var events = new ArrayList<Event>();
        if (d.getCreatedAt()!=null) events.add(new Event("CREATED", d.getCreatedAt().toInstant(), "Đơn được tạo"));
        if ("CONFIRMED".equalsIgnoreCase(d.getStatus()) || "PAID".equalsIgnoreCase(d.getStatus()))
            events.add(new Event("CONFIRMED", (d.getCreatedAt()!=null? d.getCreatedAt().toInstant():null), "Xác nhận giữ ghế"));
        if (d.getPaidAt()!=null) events.add(new Event("PAID", d.getPaidAt().toInstant(),
                (d.getPaymentMethod()!=null? d.getPaymentMethod() : "Thanh toán")));
        if ("FAILED".equalsIgnoreCase(d.getStatus()))
            events.add(new Event("FAILED", (d.getPaidAt()!=null? d.getPaidAt().toInstant() : d.getCreatedAt().toInstant()), "Thanh toán thất bại"));
        if ("CANCELLED".equalsIgnoreCase(d.getStatus()))
            events.add(new Event("CANCELLED", d.getCreatedAt().toInstant(), "Đơn bị hủy"));

        return new Detail(
                d.getId(), d.getUserId(), d.getShowtimeId(),
                d.getCustomerName(), d.getCustomerEmail(),
                d.getQuantity()==null?0:d.getQuantity(),
                d.getUnitPrice(), d.getTotalPrice(),
                d.getStatus(), d.getPaymentMethod(), d.getPaymentTxnId(),
                d.getCreatedAt()==null?null:d.getCreatedAt().toInstant(),
                d.getPaidAt()==null?null:d.getPaidAt().toInstant(),
                d.getStartTime()==null?null:d.getStartTime().toInstant(),
                d.getCinemaId(), d.getCinemaName(),
                d.getMovieId(), d.getMovieTitle(), d.getShowtimePrice(),
                events
        );
    }
}
