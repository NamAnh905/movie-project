package com.example.movie.dto.response.shared;

public class ShowtimeAvailabilityResponse {
    public int capacity;
    public long booked;
    public long remaining;
    public ShowtimeAvailabilityResponse() {}
    public ShowtimeAvailabilityResponse(int capacity, long booked) {
        this.capacity = capacity;
        this.booked = booked;
        this.remaining = capacity - booked;
    }
}
