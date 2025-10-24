package com.example.movie.service.booking;

import com.example.movie.dto.response.booking.AvailabilityDTO;
import com.example.movie.dto.request.booking.BookingRequest;
import com.example.movie.dto.response.booking.BookingDetailResponse;
import com.example.movie.dto.response.booking.BookingHistoryItemDTO;
import com.example.movie.dto.response.booking.BookingResponse;
import com.example.movie.model.booking.Booking;
import com.example.movie.model.cinema.Showtime;
import com.example.movie.model.user.User;
import com.example.movie.repository.booking.BookingRepository;
import com.example.movie.repository.cinema.ShowtimeRepository;
import com.example.movie.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          ShowtimeRepository showtimeRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.userRepository = userRepository;
    }

    /** Lấy tình trạng ghế theo mô hình sold_seats (KHÔNG dùng SUM(bookings)) */
    @Transactional(readOnly = true)
    public AvailabilityDTO getAvailability(Long showtimeId) {
        Showtime st = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại"));
        int capacity = st.getCapacity() != null ? st.getCapacity() : 50;
        int sold     = st.getSoldSeats() != null ? st.getSoldSeats() : 0;
        return new AvailabilityDTO(capacity, sold);
    }

    /** Đặt vé: KHÓA suất → kiểm tra → tăng sold_seats → tạo booking (snapshot tên/email nếu có user) */
    @Transactional
    public BookingResponse createBooking(BookingRequest req, Long userIdIfAny) {
        if (req.showtimeId == null || req.quantity == null)
            throw new IllegalArgumentException("Thiếu showtimeId hoặc quantity");
        if (req.quantity < 1 || req.quantity > 10)
            throw new IllegalArgumentException("Số vé không hợp lệ (1–10)");

        // 1) Khóa suất chiếu để tránh overbooking
        Showtime st = showtimeRepository.findByIdForUpdate(req.showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại"));

        int capacity = st.getCapacity() != null ? st.getCapacity() : 50;
        int sold     = st.getSoldSeats() != null ? st.getSoldSeats() : 0;
        int remain   = capacity - sold;
        if (remain < req.quantity) throw new IllegalStateException("Không đủ chỗ");

        // 2) Trừ ghế: tăng sold_seats
        st.setSoldSeats(sold + req.quantity);
        // (Trong @Transactional, không cần save(st) thủ công)

        // 3) Tính tiền
        BigDecimal unit  = st.getPrice() != null ? st.getPrice() : BigDecimal.ZERO;
        BigDecimal total = unit.multiply(BigDecimal.valueOf(req.quantity));

        // 4) Tạo booking (tránh lambda để không cần biến effectively final)
        Booking booking = new Booking();
        if (userIdIfAny != null) {
            User u = userRepository.findById(userIdIfAny).orElse(null);
            if (u != null) {
                booking.setUser(u);
                booking.setCustomerName(pickName(u));  // snapshot tên
                booking.setCustomerEmail(u.getEmail()); // snapshot email
            }
        } else {
            booking.setCustomerName(req.customerName);
            booking.setCustomerEmail(req.customerEmail);
        }

        booking.setShowtime(st);
        booking.setQuantity(req.quantity);
        booking.setUnitPrice(unit);
        booking.setTotalPrice(total);
        booking.setStatus("CONFIRMED");

        Booking saved = bookingRepository.save(booking);

        int remainingAfter = capacity - (st.getSoldSeats() != null ? st.getSoldSeats() : 0);
        return new BookingResponse(saved.getId(), saved.getStatus(), unit, total, remainingAfter);
    }

    // ==== helpers ====
    private String pickName(User u) {
        if (u == null) return null;
        if (notBlank(u.getFullName())) return u.getFullName();
        return u.getUsername();
    }
    private boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }

    // ==== queries ====
    public Booking getOne(Long id) {
        // Dùng join fetch để có movie/cinema/startTime cho FE
        return bookingRepository.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking không tồn tại"));
    }

    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
    }

    public List<BookingHistoryItemDTO> history(Long userId) {
        return bookingRepository.findHistoryByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<BookingDetailResponse> myBookings(Long userId) {
        return bookingRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream().map(BookingDetailResponse::from).toList();
    }
}
