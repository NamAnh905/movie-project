package com.example.movie.repository.revenue;

import com.example.movie.model.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface RevenueRepository extends JpaRepository<Booking, Long> {

    // ===== Projections (interface-based) =====
    interface RevenueSummaryRow {
        BigDecimal getRevenue();
        Long getTickets();
        Long getBookings();
    }

    interface RevenuePointRow {
        java.sql.Date getPeriod();   // 👈 thay vì java.util.Date
        java.math.BigDecimal getRevenue();
        Long getTickets();
        Long getBookings();
    }

    interface RevenueByCinemaRow {
        Long getCinemaId();
        String getCinemaName();
        BigDecimal getRevenue();
        Long getTickets();
        Long getBookings();
    }

    interface RevenueByMovieRow {
        Long getMovieId();
        String getMovieTitle();
        BigDecimal getRevenue();
        Long getTickets();
        Long getBookings();
    }

    // ===== Tổng quan =====
    @Query(value = """
        SELECT
          COALESCE(SUM(b.total_price),0) AS revenue,
          COALESCE(SUM(b.quantity),0)    AS tickets,
          COUNT(*)                        AS bookings
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        WHERE (:from IS NULL OR b.created_at >= :from)
          AND (:to   IS NULL OR b.created_at < DATE_ADD(:to, INTERVAL 1 DAY))
          AND (:cinemaId IS NULL OR s.cinema_id = :cinemaId)
          AND (:movieId  IS NULL OR s.movie_id  = :movieId)
          AND (b.status IN ('CONFIRMED','PAID'))
          AND (:onlyPaid = FALSE OR b.paid_at IS NOT NULL)
        """, nativeQuery = true)
    RevenueSummaryRow findSummary(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("cinemaId") Long cinemaId,
            @Param("movieId") Long movieId,
            @Param("onlyPaid") boolean onlyPaid
    );

    // ===== Theo ngày =====
    @Query(value = """
        SELECT
          DATE(b.created_at)              AS period,
          SUM(b.total_price)              AS revenue,
          SUM(b.quantity)                 AS tickets,
          COUNT(*)                        AS bookings
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        WHERE (:from IS NULL OR b.created_at >= :from)
          AND (:to   IS NULL OR b.created_at < DATE_ADD(:to, INTERVAL 1 DAY))
          AND (:cinemaId IS NULL OR s.cinema_id = :cinemaId)
          AND (:movieId  IS NULL OR s.movie_id  = :movieId)
          AND (b.status IN ('CONFIRMED','PAID'))
          AND (:onlyPaid = FALSE OR b.paid_at IS NOT NULL)
        GROUP BY DATE(b.created_at)
        ORDER BY period
        """, nativeQuery = true)
    List<RevenuePointRow> findByDay(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("cinemaId") Long cinemaId,
            @Param("movieId") Long movieId,
            @Param("onlyPaid") boolean onlyPaid
    );

    // ===== Theo tháng (period = ngày đầu tháng) =====
    @Query(value = """
        SELECT
          STR_TO_DATE(CONCAT(YEAR(b.created_at),'-',LPAD(MONTH(b.created_at),2,'0'),'-01'), '%Y-%m-%d') AS period,
          SUM(b.total_price)              AS revenue,
          SUM(b.quantity)                 AS tickets,
          COUNT(*)                        AS bookings
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        WHERE (:from IS NULL OR b.created_at >= :from)
          AND (:to   IS NULL OR b.created_at < DATE_ADD(:to, INTERVAL 1 DAY))
          AND (:cinemaId IS NULL OR s.cinema_id = :cinemaId)
          AND (:movieId  IS NULL OR s.movie_id  = :movieId)
          AND (b.status IN ('CONFIRMED','PAID'))
          AND (:onlyPaid = FALSE OR b.paid_at IS NOT NULL)
        GROUP BY YEAR(b.created_at), MONTH(b.created_at)
        ORDER BY period
        """, nativeQuery = true)
    List<RevenuePointRow> findByMonth(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("cinemaId") Long cinemaId,
            @Param("movieId") Long movieId,
            @Param("onlyPaid") boolean onlyPaid
    );

    // ===== Theo rạp =====
    @Query(value = """
        SELECT
          c.id                             AS cinemaId,
          c.name                           AS cinemaName,
          SUM(b.total_price)               AS revenue,
          SUM(b.quantity)                  AS tickets,
          COUNT(*)                         AS bookings
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        JOIN cinemas   c ON c.id = s.cinema_id
        WHERE (:from IS NULL OR b.created_at >= :from)
          AND (:to   IS NULL OR b.created_at < DATE_ADD(:to, INTERVAL 1 DAY))
          AND (:cinemaId IS NULL OR s.cinema_id = :cinemaId)
          AND (:movieId  IS NULL OR s.movie_id  = :movieId)
          AND (b.status IN ('CONFIRMED','PAID'))
          AND (:onlyPaid = FALSE OR b.paid_at IS NOT NULL)
        GROUP BY c.id, c.name
        ORDER BY revenue DESC
        """, nativeQuery = true)
    List<RevenueByCinemaRow> findByCinema(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("cinemaId") Long cinemaId,
            @Param("movieId") Long movieId,
            @Param("onlyPaid") boolean onlyPaid
    );

    // ===== Theo phim =====
    @Query(value = """
        SELECT
          m.id                             AS movieId,
          m.title                          AS movieTitle,
          SUM(b.total_price)               AS revenue,
          SUM(b.quantity)                  AS tickets,
          COUNT(*)                         AS bookings
        FROM bookings b
        JOIN showtimes s ON s.id = b.showtime_id
        JOIN movies    m ON m.id = s.movie_id
        WHERE (:from IS NULL OR b.created_at >= :from)
          AND (:to   IS NULL OR b.created_at < DATE_ADD(:to, INTERVAL 1 DAY))
          AND (:cinemaId IS NULL OR s.cinema_id = :cinemaId)
          AND (:movieId  IS NULL OR s.movie_id  = :movieId)
          AND (b.status IN ('CONFIRMED','PAID'))
          AND (:onlyPaid = FALSE OR b.paid_at IS NOT NULL)
        GROUP BY m.id, m.title
        ORDER BY revenue DESC
        """, nativeQuery = true)
    List<RevenueByMovieRow> findByMovie(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("cinemaId") Long cinemaId,
            @Param("movieId") Long movieId,
            @Param("onlyPaid") boolean onlyPaid
    );
}
