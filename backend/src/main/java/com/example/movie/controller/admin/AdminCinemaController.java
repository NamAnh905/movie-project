package com.example.movie.controller.admin;

import com.example.movie.dto.request.admin.AdminCinemaRequest;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.CinemaResponse;
import com.example.movie.service.cinema.AdminCinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cinemas")
@PreAuthorize("hasAuthority('MANAGE_CINEMA')") // Chuẩn phân quyền động RBAC
@RequiredArgsConstructor
public class AdminCinemaController {

    private final AdminCinemaService service;

    @PostMapping
    public ApiResponse<CinemaResponse> create(@RequestBody @Valid AdminCinemaRequest req) {
        return ApiResponse.success(service.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<CinemaResponse> update(@PathVariable Long id, @RequestBody @Valid AdminCinemaRequest req) {
        return ApiResponse.success(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.<Void>builder().message("Xóa rạp phim thành công").build();
    }
}