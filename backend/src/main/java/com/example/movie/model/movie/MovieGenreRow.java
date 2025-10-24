package com.example.movie.model.movie;

import jakarta.persistence.*;

@Entity
@Table(name = "movie_genres")
public class MovieGenreRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "genre_id")
    private Long genreId;

    public MovieGenreRow() {}
    public MovieGenreRow(Long movieId, Long genreId) { this.movieId = movieId; this.genreId = genreId; }

    public Long getId() { return id; }
    public Long getMovieId() { return movieId; }
    public Long getGenreId() { return genreId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public void setGenreId(Long genreId) { this.genreId = genreId; }
}
