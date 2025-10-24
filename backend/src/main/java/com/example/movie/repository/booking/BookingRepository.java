package com.example.movie.repository.booking;

import com.example.movie.dto.response.booking.BookingHistoryItemDTO;
import com.example.movie.model.booking.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // (Tùy — nếu bạn vẫn cần cách đếm ghế bằng SUM)
    @Query("""
        SELECT COALESCE(SUM(b.quantity), 0)
        FROM Booking b
        WHERE b.showtime.id = :showtimeId
          AND b.status IN ('PENDING','CONFIRMED','PAID')
    """)
    long sumBookedQuantity(@Param("showtimeId") Long showtimeId);

    // Chi tiết đơn: join fetch để tránh LazyInitializationException khi map DTO
    @Query("""
        select b from Booking b
          join fetch b.showtime s
          join fetch s.movie
          join fetch s.cinema
        where b.id = :id
    """)
    Optional<Booking> findDetailById(@Param("id") Long id);

    // Lịch sử đặt vé của 1 user (DTO gọn để FE hiển thị list)
    @Query("""
        select new com.example.movie.dto.response.booking.BookingHistoryItemDTO(
            b.id, s.movie.title, s.cinema.name, s.startTime, b.quantity, b.totalPrice, b.status
        )
        from Booking b
          join b.showtime s
        where b.user.id = :userId
        order by b.createdAt desc
    """)
    List<BookingHistoryItemDTO> findHistoryByUserId(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    // Dùng cho chỗ cần entity đầy đủ (không join fetch)
    List<Booking> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}
