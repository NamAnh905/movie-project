package com.example.movie.repository;

import com.example.movie.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.showtime s
        JOIN FETCH s.movie
        JOIN FETCH s.cinema
        WHERE b.id = :id
    """)
    Optional<Booking> findDetailById(@Param("id") Long id);

    @Query(value = """
        SELECT b FROM Booking b
        JOIN FETCH b.showtime s
        JOIN FETCH s.movie
        JOIN FETCH s.cinema
        WHERE b.user.id = :userId
        """,
            countQuery = "SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId")
    Page<Booking> findHistoryByUserId(@Param("userId") Long userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        SELECT COALESCE(SUM(b.quantity), 0)
        FROM Booking b
        WHERE b.showtime.id = :showtimeId
          AND b.status IN ('PENDING','CONFIRMED','PAID')
    """)
    long sumBookedQuantity(@Param("showtimeId") Long showtimeId);

    @Query(value = """
        SELECT b FROM Booking b
        LEFT JOIN FETCH b.showtime s
        LEFT JOIN FETCH s.cinema c
        LEFT JOIN FETCH s.movie m
        WHERE (:from IS NULL OR b.createdAt >= :from)
          AND (:to IS NULL OR b.createdAt < :to)
          AND (:cinemaId IS NULL OR c.id = :cinemaId)
          AND (:movieId IS NULL OR m.id = :movieId)
          AND (:showtimeId IS NULL OR s.id = :showtimeId)
          AND (:status IS NULL OR b.status = :status)
          AND (:paymentMethod IS NULL OR b.paymentMethod = :paymentMethod)
          AND (:q IS NULL OR b.customerEmail LIKE CONCAT('%', :q, '%') 
               OR b.customerName LIKE CONCAT('%', :q, '%') 
               OR b.paymentTxnId LIKE CONCAT('%', :q, '%'))
        ORDER BY b.createdAt DESC
    """, countQuery = """
        SELECT COUNT(b) FROM Booking b
        LEFT JOIN b.showtime s
        LEFT JOIN s.cinema c
        LEFT JOIN s.movie m
        WHERE (:from IS NULL OR b.createdAt >= :from)
          AND (:to IS NULL OR b.createdAt < :to)
          AND (:cinemaId IS NULL OR c.id = :cinemaId)
          AND (:movieId IS NULL OR m.id = :movieId)
          AND (:showtimeId IS NULL OR s.id = :showtimeId)
          AND (:status IS NULL OR b.status = :status)
          AND (:paymentMethod IS NULL OR b.paymentMethod = :paymentMethod)
          AND (:q IS NULL OR b.customerEmail LIKE CONCAT('%', :q, '%') 
               OR b.customerName LIKE CONCAT('%', :q, '%') 
               OR b.paymentTxnId LIKE CONCAT('%', :q, '%'))
    """)
    Page<Booking> searchAdminBookings(
            @Param("from") Instant from, @Param("to") Instant to,
            @Param("cinemaId") Long cinemaId, @Param("movieId") Long movieId, @Param("showtimeId") Long showtimeId,
            @Param("status") String status, @Param("paymentMethod") String paymentMethod, @Param("q") String q,
            Pageable pageable);

    List<Booking> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}