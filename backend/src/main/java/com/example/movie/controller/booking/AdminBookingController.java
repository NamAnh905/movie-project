package com.example.movie.controller.booking;

import com.example.movie.common.ApiResponse;
import com.example.movie.dto.response.booking.AdminBookingDTOs.Detail;
import com.example.movie.dto.response.booking.AdminBookingDTOs.PageResponse;
import com.example.movie.dto.response.booking.AdminBookingDTOs.ListItem;
import com.example.movie.service.booking.AdminBookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final AdminBookingService service;
    public AdminBookingController(AdminBookingService service){ this.service = service; }

    @GetMapping
    public ApiResponse<PageResponse<ListItem>> search(
            @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required=false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required=false) Long cinemaId,
            @RequestParam(required=false) Long movieId,
            @RequestParam(required=false) Long showtimeId,
            @RequestParam(required=false) String status,
            @RequestParam(required=false) String paymentMethod,
            @RequestParam(required=false) String q,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size
    ){
        return ApiResponse.ok(service.search(from,to,cinemaId,movieId,showtimeId,status,paymentMethod,q,page,size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Detail> detail(@PathVariable long id){
        var d = service.getDetail(id);
        return d==null ? ApiResponse.fail("Không tìm thấy đơn") : ApiResponse.ok(d);
    }
}
