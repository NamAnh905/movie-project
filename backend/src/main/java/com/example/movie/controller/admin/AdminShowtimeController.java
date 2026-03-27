package com.example.movie.controller.admin;

import com.example.movie.dto.request.admin.AdminShowtimeRequest;
import com.example.movie.dto.response.admin.AdminShowtimeBatchResponse;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.dto.response.shared.ShowtimeResponse;
import com.example.movie.service.showtime.AdminShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/showtimes")
@PreAuthorize("hasAuthority('MANAGE_SHOWTIME')") // CHUẨN RBAC
@RequiredArgsConstructor
public class AdminShowtimeController {

    private final AdminShowtimeService service;

    @PostMapping("/batch")
    public ApiResponse<AdminShowtimeBatchResponse> createBatch(@Valid @RequestBody AdminShowtimeRequest req) {
        return ApiResponse.success(service.createBatch(req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.<Void>builder().message("Xóa thành công").build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ShowtimeResponse>> search(
            @RequestParam(required=false) Long movieId,
            @RequestParam(required=false) Long cinemaId,
            @RequestParam(required=false) String state,
            @RequestParam(required=false) String date,
            Pageable pageable) {
        LocalDate d = (date != null && !date.isBlank()) ? LocalDate.parse(date) : null;
        return ApiResponse.success(service.search(movieId, cinemaId, state, d, pageable));
    }
}