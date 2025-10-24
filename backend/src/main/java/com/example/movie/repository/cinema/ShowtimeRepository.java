package com.example.movie.repository.cinema;

import com.example.movie.model.cinema.Showtime;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // Dành cho FE user: lấy theo rạp + ngày (đã fetch movie để tránh Lazy)
    @Query("""
        select s from Showtime s
        join fetch s.movie m
        where s.cinema.id = :cinemaId
          and s.startTime >= :start
          and s.startTime <  :end
    """)
    List<Showtime> findPublicWithMovie(@Param("cinemaId") Long cinemaId,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    @Query("""
        select s from Showtime s
        join fetch s.cinema c
        where s.movie.id = :movieId
          and s.startTime >= :start
          and s.startTime <  :end
    """)
    List<Showtime> findPublicByMovieWithCinema(@Param("movieId") Long movieId,
                                               @Param("start") java.time.LocalDateTime start,
                                               @Param("end")   java.time.LocalDateTime end);

    // Dành cho màn admin/search: lọc linh hoạt theo rạp/phim/khoảng thời gian
    @Query("""
        select s from Showtime s
        where (:cinemaId is null or s.cinema.id = :cinemaId)
          and (:movieId  is null or s.movie.id  = :movieId)
          and (:start    is null or s.startTime >= :start)
          and (:end      is null or s.startTime <  :end)
    """)
    Page<Showtime> search(@Param("cinemaId") Long cinemaId,
                          @Param("movieId")  Long movieId,
                          @Param("start")    LocalDateTime start,
                          @Param("end")      LocalDateTime end,
                          Pageable pageable);
    boolean existsByCinema_IdAndMovie_IdAndStartTime(Long cinemaId, Long movieId, LocalDateTime startTime);

    Optional<Showtime> findFirstByCinema_IdAndMovie_IdAndStartTimeBetween(
            Long cinemaId, Long movieId, LocalDateTime from, LocalDateTime to);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Showtime s where s.id = :id")
    Optional<Showtime> findByIdForUpdate(@Param("id") Long id);
}
