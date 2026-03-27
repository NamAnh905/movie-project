package com.example.movie.dto.response.shared;

import lombok.Builder;

@Builder
public record CinemaResponse(Long id, String name, String address, String status) {}