package com.example.movie.controller.client;

import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.GenreResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.service.genre.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    @GetMapping
    public ApiResponse<PageResponse<GenreResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(service.list(q, PageRequest.of(page, size)));
    }

    @GetMapping("/all")
    public ApiResponse<List<GenreResponse>> listAll() {
        return ApiResponse.success(service.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<GenreResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }
}