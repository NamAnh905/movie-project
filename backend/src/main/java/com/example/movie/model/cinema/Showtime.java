package com.example.movie.model.cinema;

import com.example.movie.model.movie.Movie;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes",
        indexes = {
                @Index(name="idx_st_movie_time",  columnList="movie_id,start_time"),
                @Index(name="idx_st_cinema_time", columnList="cinema_id,start_time")
        })
public class Showtime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="movie_id", nullable=false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="cinema_id", nullable=false)
    private Cinema cinema;

    @Column(name="start_time", nullable=false) private LocalDateTime startTime;
    @Column(name="end_time",   nullable=false) private LocalDateTime endTime;

    @Column(name = "sold_seats") private Integer soldSeats;
    @Column(name = "capacity")   private Integer capacity;
    @Column(precision=10, scale=2) private BigDecimal price;
    @Column(nullable=false, length=20) private String status = "OPEN";

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public Movie getMovie(){ return movie; }
    public void setMovie(Movie movie){ this.movie = movie; }

    public Cinema getCinema(){ return cinema; }
    public void setCinema(Cinema cinema){ this.cinema = cinema; }

    public LocalDateTime getStartTime(){ return startTime; }
    public void setStartTime(LocalDateTime s){ this.startTime = s; }

    public LocalDateTime getEndTime(){ return endTime; }
    public void setEndTime(LocalDateTime e){ this.endTime = e; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BigDecimal getPrice(){ return price; }
    public void setPrice(BigDecimal p){ this.price = p; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }

    // === NEW: soldSeats getter/setter ===
    public Integer getSoldSeats() { return soldSeats; }
    public void setSoldSeats(Integer soldSeats) { this.soldSeats = soldSeats; }

    // (Tuỳ chọn) helper tránh null:
    public int getSoldSeatsOrZero() { return soldSeats == null ? 0 : soldSeats; }
}
