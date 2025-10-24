package com.example.movie.model.movie;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name="genres",
        uniqueConstraints = {@UniqueConstraint(name="uq_genres_name", columnNames="name"),
                @UniqueConstraint(name="uq_genres_slug", columnNames="slug")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Genre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(nullable=false, length=120)
    private String slug;

    @Column(nullable=false, updatable=false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    @PreUpdate void preUpdate(){ this.updatedAt = Instant.now(); }
}