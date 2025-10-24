package com.example.movie.repository.movie;

import com.example.movie.model.movie.MovieGenreRow;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MovieGenreRepository extends JpaRepository<MovieGenreRow, Long> {

    @Modifying
    @Query(value = "DELETE FROM movie_genres WHERE movie_id = :movieId", nativeQuery = true)
    void deleteAllByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Query(value = "INSERT INTO movie_genres(movie_id, genre_id) VALUES(:movieId, :genreId)", nativeQuery = true)
    void insertOne(@Param("movieId") Long movieId, @Param("genreId") Long genreId);

    // ✅ mới: phục vụ toDTOEnriched()
    @Query(value = "SELECT genre_id FROM movie_genres WHERE movie_id = :movieId", nativeQuery = true)
    List<Long> findGenreIdsByMovieId(@Param("movieId") Long movieId);
}
