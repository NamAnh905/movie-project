package com.example.movie.dto.response.booking;

public class AvailabilityDTO {
    public int capacity;
    public long booked;
    public long remaining;
    public AvailabilityDTO() {}
    public AvailabilityDTO(int capacity, long booked) {
        this.capacity = capacity;
        this.booked = booked;
        this.remaining = capacity - booked;
    }
}
