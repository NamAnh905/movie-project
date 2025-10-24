package com.example.movie.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username: VARCHAR(100) NOT NULL, UNIQUE -> nên có unique
    @Column(nullable = false, length = 100, unique = true)
    private String username;

    // password: VARCHAR(255) NOT NULL (dùng cho auth hiện tại)
    @Column(nullable = false, length = 255)
    private String password;

    // Nếu DB có cả cột password_hash, vẫn map để không lỗi schema
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    // status: VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'
    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    // full_name: VARCHAR(255) NULL
    @Column(name = "full_name", length = 255)
    private String fullName;

    // email: VARCHAR(255) NOT NULL (theo dump của bạn là NOT NULL)
    @Column(nullable = false, length = 255)
    private String email;

    // role: VARCHAR(30) NOT NULL DEFAULT 'USER'
    @Column(nullable = false, length = 30)
    private String role = "USER";

    // enabled: TINYINT(1) NOT NULL DEFAULT 1
    @Column(nullable = false)
    private Boolean enabled = true;

    // created_at / updated_at
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
