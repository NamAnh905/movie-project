package com.example.movie.repository;

import com.example.movie.entity.Cinema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<Cinema> findByStatus(String status);

    // Tìm kiếm rạp chiếu phim với JPQL
    @Query("SELECT c FROM Cinema c WHERE :q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Cinema> search(@Param("q") String q, Pageable pageable);
}