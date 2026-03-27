package com.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes", indexes = {
        @Index(name="idx_st_movie_time",  columnList="movie_id,start_time"),
        @Index(name="idx_st_cinema_time", columnList="cinema_id,start_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="movie_id", nullable=false)
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cinema_id", nullable=false)
    Cinema cinema;

    @Column(name="start_time", nullable=false)
    LocalDateTime startTime;

    @Column(name="end_time", nullable=false)
    LocalDateTime endTime;

    @Builder.Default
    @Column(name = "sold_seats")
    Integer soldSeats = 0;

    @Column(name = "capacity")
    Integer capacity;

    @Column(precision=10, scale=2)
    BigDecimal price;

    @Builder.Default
    @Column(nullable=false, length=20)
    String status = "OPEN";
}