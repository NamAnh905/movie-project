package com.example.movie.repository;

import com.example.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
        SELECT DISTINCT m FROM Movie m
        LEFT JOIN m.genres g
        WHERE (:genreId IS NULL OR g.id = :genreId)
          AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:status IS NULL OR m.status = :status)
    """)
    Page<Movie> search(@Param("status") String status,
                       @Param("genreId") Long genreId,
                       @Param("q") String q,
                       Pageable pageable);

    List<Movie> findByStatusOrderByReleaseDateDesc(String status);
}