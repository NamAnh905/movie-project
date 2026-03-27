package com.example.movie.controller.admin;

import com.example.movie.dto.request.admin.AdminGenreRequest;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.GenreResponse;
import com.example.movie.service.genre.AdminGenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/genres")
@PreAuthorize("hasAuthority('MANAGE_GENRE')")
@RequiredArgsConstructor
public class AdminGenreController {

    private final AdminGenreService service;

    @PostMapping
    public ApiResponse<GenreResponse> create(@RequestBody @Valid AdminGenreRequest req) {
        return ApiResponse.success(service.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<GenreResponse> update(@PathVariable Long id, @RequestBody @Valid AdminGenreRequest req) {
        return ApiResponse.success(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.<Void>builder().message("Xóa thể loại thành công").build();
    }
}