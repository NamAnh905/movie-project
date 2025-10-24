package com.example.movie.repository.movie;

import com.example.movie.model.movie.Genre;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query(value = """
        SELECT g.name FROM genres g
        JOIN movie_genres mg ON g.id = mg.genre_id
        WHERE mg.movie_id = :movieId
        ORDER BY g.name
        """, nativeQuery = true)
    List<String> findNamesByMovieId(@Param("movieId") Long movieId);

    @Query(value = """
        SELECT g.id FROM genres g
        JOIN movie_genres mg ON g.id = mg.genre_id
        WHERE mg.movie_id = :movieId
        ORDER BY g.id
        """, nativeQuery = true)
    List<Long> findIdsByMovieId(@Param("movieId") Long movieId);
}
