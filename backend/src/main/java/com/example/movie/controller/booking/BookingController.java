package com.example.movie.controller.booking;

import com.example.movie.dto.request.booking.BookingRequest;
import com.example.movie.dto.response.booking.AvailabilityDTO;
import com.example.movie.dto.response.booking.BookingDetailResponse;
import com.example.movie.dto.response.booking.BookingHistoryItemDTO;
import com.example.movie.dto.response.booking.BookingResponse;
import com.example.movie.model.booking.Booking;
import com.example.movie.model.user.User;
import com.example.movie.repository.user.UserRepository;
import com.example.movie.service.booking.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    // ==== FE form: xem tồn suất chiếu ====
    @GetMapping("/showtimes/{id}/availability")
    public AvailabilityDTO availability(@PathVariable("id") Long showtimeId) {
        return bookingService.getAvailability(showtimeId);
    }

    // ==== Tạo đơn (sẽ tích hợp VNPAY sau) ====
    @PostMapping("/bookings")
    public ResponseEntity<?> create(@RequestBody BookingRequest req, Authentication auth) {
        Long userId = null;
        if (auth != null && auth.isAuthenticated()) {
            userId = userRepository.findByUsername(auth.getName())
                    .map(User::getId).orElse(null);
        }
        BookingResponse res = bookingService.createBooking(req, userId);
        return ResponseEntity.ok(res);
    }

    // ==== Lấy chi tiết đơn cho màn /bookings/:id ====
    @GetMapping("/bookings/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long id) {
        Booking b = bookingService.getOne(id);
        return ResponseEntity.ok(BookingDetailResponse.from(b));
    }

    @GetMapping("/bookings/mine")
    public ResponseEntity<List<BookingHistoryItemDTO>> myBookings(Authentication auth) {
        Long userId = userRepository.findByUsername(auth.getName())
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return ResponseEntity.ok(bookingService.history(userId));
    }
}
