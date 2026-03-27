package com.example.movie.dto.request.client;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreationRequest {

    @NotNull(message = "ShowtimeId cannot be empty.")
    Long showtimeId;

    @NotNull(message = "Quantity cannot be empty.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Max(value = 10, message = "Quantity cannot exceed 10.")
    Integer quantity;

    String customerName;
    String customerEmail;
}