package com.example.movie.repository.cinema;

import com.example.movie.model.cinema.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    boolean existsByName(String name);
    List<Cinema> findByStatus(String status); // thêm
}
