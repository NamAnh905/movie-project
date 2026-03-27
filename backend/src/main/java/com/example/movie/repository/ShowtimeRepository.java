package com.example.movie.repository;

import com.example.movie.entity.Showtime;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("""
        select s from Showtime s join fetch s.movie m
        where s.cinema.id = :cinemaId and s.startTime >= :start and s.startTime < :end
    """)
    List<Showtime> findPublicWithMovie(@Param("cinemaId") Long cinemaId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
        select s from Showtime s join fetch s.cinema c
        where s.movie.id = :movieId and s.startTime >= :start and s.startTime < :end
    """)
    List<Showtime> findPublicByMovieWithCinema(@Param("movieId") Long movieId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // FIX LỖI PHÂN TRANG: Lọc trạng thái trực tiếp bằng SQL
    @Query("""
        select s from Showtime s
        where (:cinemaId is null or s.cinema.id = :cinemaId)
          and (:movieId  is null or s.movie.id  = :movieId)
          and (cast(:start as timestamp) is null or s.startTime >= :start)
          and (cast(:end as timestamp) is null or s.startTime < :end)
          and (:state is null 
               or (:state = 'UPCOMING' and s.startTime > :now)
               or (:state = 'NOW_SHOWING' and s.startTime <= :now and s.endTime > :now)
               or (:state = 'ENDED' and s.endTime <= :now))
    """)
    Page<Showtime> searchAdmin(
            @Param("cinemaId") Long cinemaId, @Param("movieId") Long movieId,
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
            @Param("state") String state, @Param("now") LocalDateTime now,
            Pageable pageable);

    Optional<Showtime> findFirstByCinema_IdAndMovie_IdAndStartTimeBetween(
            Long cinemaId, Long movieId, LocalDateTime from, LocalDateTime to);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Showtime s where s.id = :id")
    Optional<Showtime> findByIdForUpdate(@Param("id") Long id);
}