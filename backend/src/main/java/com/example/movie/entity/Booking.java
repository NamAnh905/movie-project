package com.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    Showtime showtime;

    String customerName;
    String customerEmail;

    @Column(nullable = false)
    Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    BigDecimal totalPrice;

    @Builder.Default
    @Column(length = 20, nullable = false)
    String status = "CONFIRMED"; // PENDING/CONFIRMED/CANCELLED/PAID

    @Column(name = "payment_method", length = 20)
    String paymentMethod;

    @Column(name = "payment_txn_id", length = 100)
    String paymentTxnId;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "paid_at")
    Instant paidAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    @Column(name = "client_return_url", length = 512)
    String clientReturnUrl;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;
}