package com.example.movie.service.booking;

import com.example.movie.dto.response.admin.AdminBookingDetailResponse;
import com.example.movie.dto.response.admin.AdminBookingListResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.Booking;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.booking.AdminBookingMapper;
import com.example.movie.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBookingService {

    private final BookingRepository bookingRepository;
    private final AdminBookingMapper adminBookingMapper;

    public PageResponse<AdminBookingListResponse> search(
            LocalDate from, LocalDate to, Long cinemaId, Long movieId, Long showtimeId,
            String status, String paymentMethod, String q, int page, int size) {

        Instant fromInstant = from == null ? null : from.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toInstant = to == null ? null : to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        String keyword = (q == null || q.isBlank()) ? null : q.trim();

        Page<Booking> pageData = bookingRepository.searchAdminBookings(
                fromInstant, toInstant, cinemaId, movieId, showtimeId,
                status, paymentMethod, keyword,
                PageRequest.of(Math.max(page, 0), Math.max(size, 1))
        );

        return adminBookingMapper.toPageResponse(pageData); // Service mỏng dính!
    }

    public AdminBookingDetailResponse getDetail(Long id) {
        Booking booking = bookingRepository.findDetailById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Đổi thành BOOKING_NOT_FOUND nếu bạn đã có

        return adminBookingMapper.toDetailResponse(booking);
    }
}