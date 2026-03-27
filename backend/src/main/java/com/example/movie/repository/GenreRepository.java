package com.example.movie.repository;

import com.example.movie.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    @Query("""
            SELECT g
            FROM Genre g
            WHERE :q IS NULL
            OR LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%'))
            """
    )
    Page<Genre> search(@Param("q") String q, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);
}