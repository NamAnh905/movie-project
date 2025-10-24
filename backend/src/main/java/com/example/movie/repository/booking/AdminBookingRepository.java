package com.example.movie.repository.booking;

import com.example.movie.model.booking.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface AdminBookingRepository extends JpaRepository<Booking, Long> {

    interface ListRow {
        Long getId();
        String getStatus();
        Integer getQuantity();
        java.math.BigDecimal getTotalPrice();
        String getPaymentMethod();
        String getPaymentTxnId();
        String getCustomerName();
        String getCustomerEmail();
        java.util.Date getCreatedAt();
        java.util.Date getPaidAt();
        Long getShowtimeId();
        java.util.Date getStartTime();
        Long getCinemaId();
        String getCinemaName();
        Long getMovieId();
        String getMovieTitle();
    }

    @Query(
            value = """
        SELECT  b.id                AS id,
                b.status           AS status,
                b.quantity         AS quantity,
                b.total_price      AS totalPrice,
                b.payment_method   AS paymentMethod,
                b.payment_txn_id   AS paymentTxnId,
                b.customer_name    AS customerName,
                b.customer_email   AS customerEmail,
                b.created_at       AS createdAt,
                b.paid_at          AS paidAt,
                s.id               AS showtimeId,
                s.start_time       AS startTime,
                c.id               AS cinemaId,
                c.name             AS cinemaName,
                m.id               AS movieId,
                m.title            AS movieTitle
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        JOIN cinemas   c ON c.id = s.cinema_id
        JOIN movies    m ON m.id = s.movie_id
        WHERE (:from IS NULL OR b.created_at >= :from)
          AND (:to   IS NULL OR b.created_at < DATE_ADD(:to, INTERVAL 1 DAY))
          AND (:cinemaId  IS NULL OR s.cinema_id = :cinemaId)
          AND (:movieId   IS NULL OR s.movie_id  = :movieId)
          AND (:showtimeId IS NULL OR s.id       = :showtimeId)
          AND (:status IS NULL OR b.status = :status)
          AND (:paymentMethod IS NULL OR b.payment_method = :paymentMethod)
          AND (
                :q IS NULL OR
                b.customer_email LIKE CONCAT('%',:q,'%') OR
                b.customer_name  LIKE CONCAT('%',:q,'%') OR
                b.payment_txn_id LIKE CONCAT('%',:q,'%')
              )
        ORDER BY b.created_at DESC
      """,
            countQuery = """
        SELECT COUNT(*)
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        WHERE (:from IS NULL OR b.created_at >= :from)
          AND (:to   IS NULL OR b.created_at < DATE_ADD(:to, INTERVAL 1 DAY))
          AND (:cinemaId  IS NULL OR s.cinema_id = :cinemaId)
          AND (:movieId   IS NULL OR s.movie_id  = :movieId)
          AND (:showtimeId IS NULL OR s.id       = :showtimeId)
          AND (:status IS NULL OR b.status = :status)
          AND (:paymentMethod IS NULL OR b.payment_method = :paymentMethod)
          AND (
                :q IS NULL OR
                b.customer_email LIKE CONCAT('%',:q,'%') OR
                b.customer_name  LIKE CONCAT('%',:q,'%') OR
                b.payment_txn_id LIKE CONCAT('%',:q,'%')
              )
      """,
            nativeQuery = true
    )
    Page<ListRow> search(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("cinemaId") Long cinemaId,
            @Param("movieId") Long movieId,
            @Param("showtimeId") Long showtimeId,
            @Param("status") String status,
            @Param("paymentMethod") String paymentMethod,
            @Param("q") String q,
            Pageable pageable
    );

    interface DetailRow {
        Long getId();
        Long getUserId();
        Long getShowtimeId();
        String getCustomerName();
        String getCustomerEmail();
        Integer getQuantity();
        java.math.BigDecimal getUnitPrice();
        java.math.BigDecimal getTotalPrice();
        String getStatus();
        String getPaymentMethod();
        String getPaymentTxnId();
        java.util.Date getCreatedAt();
        java.util.Date getPaidAt();
        // showtime + movie + cinema
        java.util.Date getStartTime();
        Long getCinemaId();
        String getCinemaName();
        Long getMovieId();
        String getMovieTitle();
        java.math.BigDecimal getShowtimePrice();
    }

    @Query(value = """
        SELECT
          b.id, b.user_id AS userId, b.showtime_id AS showtimeId,
          b.customer_name AS customerName, b.customer_email AS customerEmail,
          b.quantity, b.unit_price AS unitPrice, b.total_price AS totalPrice,
          b.status, b.payment_method AS paymentMethod, b.payment_txn_id AS paymentTxnId,
          b.created_at AS createdAt, b.paid_at AS paidAt,
          s.start_time AS startTime,
          c.id AS cinemaId, c.name AS cinemaName,
          m.id AS movieId, m.title AS movieTitle,
          s.price AS showtimePrice
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        JOIN cinemas   c ON c.id = s.cinema_id
        JOIN movies    m ON m.id = s.movie_id
        WHERE b.id = :id
    """, nativeQuery = true)
    DetailRow findDetail(@Param("id") Long id);
}
