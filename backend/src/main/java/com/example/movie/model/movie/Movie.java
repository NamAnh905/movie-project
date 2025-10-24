package com.example.movie.model.movie;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies", indexes = {
        @Index(name = "idx_movies_title", columnList = "title"),
        @Index(name = "idx_movies_created_at", columnList = "createdAt")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=255)
    private String title;

    // Năm phát hành (tùy chọn)
    private Integer year;

    // FE dùng "duration" (phút)
    private Integer duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "poster_url", length = 512)
    private String posterUrl;

    // FE đọc status hoặc movieStatus -> ta dùng "status"
    // Giá trị gợi ý: RELEASED | COMING_SOON
    @Column(nullable=false, length=32)
    private String status = "RELEASED";

    @Column(name = "age_rating", length=32)
    private String ageRating;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(length=64)
    private String language;

    @Column(length=64)
    private String country;

    @ManyToMany
    @JoinTable(name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();

    @Column(nullable=false, updatable=false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    @PreUpdate
    void preUpdate() { this.updatedAt = Instant.now(); }
}
