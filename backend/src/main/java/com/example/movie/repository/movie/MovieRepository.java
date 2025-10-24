package com.example.movie.repository.movie;

import com.example.movie.model.movie.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Đang có: lọc theo genre + q (giữ lại để tương thích)
    @Query(value = """
        SELECT m.* FROM movies m
        JOIN movie_genres mg ON m.id = mg.movie_id
        WHERE (:genreId IS NULL OR mg.genre_id = :genreId)
          AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
        GROUP BY m.id
        """,
            countQuery = """
        SELECT COUNT(DISTINCT m.id) FROM movies m
        JOIN movie_genres mg ON m.id = mg.movie_id
        WHERE (:genreId IS NULL OR mg.genre_id = :genreId)
          AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
        """,
            nativeQuery = true)
    Page<Movie> searchByGenreAndTitle(@Param("genreId") Long genreId,
                                      @Param("q") String q,
                                      Pageable pageable);

    // MỚI: lọc theo status (+ optional genre, q)
    @Query(value = """
        SELECT m.* FROM movies m
        LEFT JOIN movie_genres mg ON m.id = mg.movie_id
        WHERE (:genreId IS NULL OR mg.genre_id = :genreId)
          AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
        GROUP BY m.id
        """,
                countQuery = """
        SELECT COUNT(DISTINCT m.id) FROM movies m
        LEFT JOIN movie_genres mg ON m.id = mg.movie_id
        WHERE (:genreId IS NULL OR mg.genre_id = :genreId)
          AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
        """,
                nativeQuery = true)
    Page<Movie> search(@Param("status") String status,
                       @Param("genreId") Long genreId,
                       @Param("q") String q,
                       Pageable pageable);

    List<Movie> findByStatusOrderByReleaseDateDesc(String status);
}
