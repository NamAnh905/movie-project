package com.example.movie.controller.client;

import com.example.movie.dto.request.client.BookingCreationRequest;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.ShowtimeAvailabilityResponse;
import com.example.movie.dto.response.client.BookingResponse;
import com.example.movie.service.booking.BookingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;

    @GetMapping("/showtimes/{id}/availability")
    public ApiResponse<ShowtimeAvailabilityResponse> availability(@PathVariable("id") Long showtimeId) {
        return ApiResponse.<ShowtimeAvailabilityResponse>builder()
                .result(bookingService.getAvailability(showtimeId))
                .build();
    }

    @PostMapping
    public ApiResponse<BookingResponse> create(@RequestBody @Valid BookingCreationRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.create(request))
                .build();
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> getOne(@PathVariable("id") Long id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getDetail(id))
                .build();
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/mine")
    public ApiResponse<List<BookingResponse>> myBookings() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyBookings())
                .build();
    }
}