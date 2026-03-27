package com.example.movie.controller.client;

import com.example.movie.dto.response.client.CinemaShowtimeResponse;
import com.example.movie.dto.response.client.MovieShowtimeResponse;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.service.showtime.ShowtimeService; // Service của Client
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService service;

    @GetMapping("/public")
    public ApiResponse<List<MovieShowtimeResponse>> getPublicShowtimes(
            @RequestParam Long cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(service.getPublicShowtimes(cinemaId, date));
    }

    @GetMapping("/public/by-movie")
    public ApiResponse<List<CinemaShowtimeResponse>> getPublicByMovie(
            @RequestParam Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(service.getPublicShowtimesByMovie(movieId, date));
    }
}