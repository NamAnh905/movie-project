package com.example.movie.model.cinema;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "cinemas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cinema {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE | INACTIVE
}
