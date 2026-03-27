package com.example.movie.controller.client;

import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.MovieResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.service.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping
    public ApiResponse<PageResponse<MovieResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(service.list(q, genreId, status, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }

    @GetMapping("/status/{status}/all")
    public ApiResponse<List<MovieResponse>> listAllByStatus(@PathVariable String status) {
        return ApiResponse.success(service.findAllByStatus(status));
    }
}