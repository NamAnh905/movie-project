package com.example.movie.service.booking;

import com.example.movie.util.SecurityUtils;
import com.example.movie.dto.request.client.BookingCreationRequest;
import com.example.movie.dto.response.shared.ShowtimeAvailabilityResponse;
import com.example.movie.dto.response.client.BookingResponse;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.booking.BookingMapper;
import com.example.movie.entity.Booking;
import com.example.movie.entity.Showtime;
import com.example.movie.entity.User;
import com.example.movie.repository.BookingRepository;
import com.example.movie.repository.ShowtimeRepository;
import com.example.movie.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class BookingService {

    BookingRepository bookingRepository;
    ShowtimeRepository showtimeRepository;
    UserRepository userRepository;
    BookingMapper bookingMapper;
    SecurityUtils securityUtils;

    public ShowtimeAvailabilityResponse getAvailability(Long showtimeId) {
        Showtime st = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        int capacity = st.getCapacity() != null ? st.getCapacity() : 50;
        int sold = st.getSoldSeats() != null ? st.getSoldSeats() : 0;
        return new ShowtimeAvailabilityResponse(capacity, sold);
    }

    @Transactional
    public BookingResponse create(BookingCreationRequest req) {
        Showtime st = showtimeRepository.findById(req.getShowtimeId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        int capacity = st.getCapacity() != null ? st.getCapacity() : 50;
        int sold = st.getSoldSeats() != null ? st.getSoldSeats() : 0;
        int remain = capacity - sold;

        if (remain < req.getQuantity()) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }

        st.setSoldSeats(sold + req.getQuantity());

        BigDecimal unit = st.getPrice() != null ? st.getPrice() : BigDecimal.ZERO;
        BigDecimal total = unit.multiply(BigDecimal.valueOf(req.getQuantity()));

        Booking booking = Booking.builder()
                .showtime(st)
                .quantity(req.getQuantity())
                .unitPrice(unit)
                .totalPrice(total)
                .status("CONFIRMED")
                .build();

        Optional<String> currentUserOpt = securityUtils.getCurrentUserLoginOptional();

        if (currentUserOpt.isPresent()) {
            User u = userRepository.findByUsername(currentUserOpt.get())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            booking.setUser(u);
            booking.setCustomerName(notBlank(u.getFullName()) ? u.getFullName() : u.getUsername());
            booking.setCustomerEmail(u.getEmail());
        } else {
            booking.setCustomerName(req.getCustomerName());
            booking.setCustomerEmail(req.getCustomerEmail());
        }

        Booking saved = bookingRepository.save(booking);

        return bookingMapper.toBookingResponse(saved);
    }

    public BookingResponse getDetail(Long id) {
        Booking booking = bookingRepository.findDetailById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_INVALID));

        checkBookingOwner(booking);

        return bookingMapper.toBookingResponse(booking);
    }

    public List<BookingResponse> getMyBookings() {
        String currentUsername = securityUtils.getCurrentUserLogin();

        User u = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return bookingRepository.findAllByUser_IdOrderByCreatedAtDesc(u.getId())
                .stream()
                .map(bookingMapper::toBookingResponse)
                .toList();
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private void checkBookingOwner(Booking booking) {
        String currentUsername = securityUtils.getCurrentUserLogin();

        boolean isAdmin = securityUtils.hasAuthority("ROLE_ADMIN");

        if (!isAdmin && booking.getUser() != null) {
            String ownerUsername = booking.getUser().getUsername();
            if (!ownerUsername.equals(currentUsername)) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
    }
}