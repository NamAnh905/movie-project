package com.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name="genres", uniqueConstraints = {
        @UniqueConstraint(name="uq_genres_name", columnNames="name"),
        @UniqueConstraint(name="uq_genres_slug", columnNames="slug")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable=false, length=100)
    String name;

    @Column(nullable=false, length=120)
    String slug;

    @Builder.Default
    @Column(nullable=false, updatable=false)
    Instant createdAt = Instant.now();

    Instant updatedAt;

    @PreUpdate
    void preUpdate(){ this.updatedAt = Instant.now(); }
}