package com.example.movie.controller.client;

import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.CinemaResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.service.cinema.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService service;

    @GetMapping
    public ApiResponse<PageResponse<CinemaResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(service.list(q, PageRequest.of(page, size)));
    }

    @GetMapping("/public")
    public ApiResponse<List<CinemaResponse>> listPublic() {
        return ApiResponse.success(service.listPublic());
    }

    @GetMapping("/{id}")
    public ApiResponse<CinemaResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }
}