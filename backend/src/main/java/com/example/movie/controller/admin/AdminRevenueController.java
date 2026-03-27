package com.example.movie.controller.admin;

import com.example.movie.dto.response.admin.AdminRevenueResponse;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.service.revenue.AdminRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/revenue")
@PreAuthorize("hasAuthority('VIEW_REVENUE')") // Chuẩn phân quyền động
@RequiredArgsConstructor
public class AdminRevenueController {

    private final AdminRevenueService service;

    @GetMapping("/overview")
    public ApiResponse<AdminRevenueResponse> getOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) Long movieId,
            @RequestParam(defaultValue = "true") boolean onlyPaid,
            @RequestParam(defaultValue = "day") String groupBy) {

        return ApiResponse.success(service.getOverview(from, to, cinemaId, movieId, onlyPaid, groupBy));
    }
}