package com.example.movie.controller.admin;

import com.example.movie.dto.request.admin.AdminMovieRequest;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.MovieResponse;
import com.example.movie.service.movie.AdminMovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/movies")
@PreAuthorize("hasAuthority('MANAGE_MOVIE')")
@RequiredArgsConstructor
public class AdminMovieController {

    private final AdminMovieService service;

    @PostMapping
    public ApiResponse<MovieResponse> create(@RequestBody @Valid AdminMovieRequest req) {
        return ApiResponse.success(service.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieResponse> update(@PathVariable Long id, @RequestBody @Valid AdminMovieRequest req) {
        return ApiResponse.success(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.<Void>builder().message("Xóa phim thành công").build();
    }
}