package com.example.movie.controller.revenue;

import com.example.movie.common.ApiResponse;
import com.example.movie.dto.response.revenue.RevenueDTOs.Overview;
import com.example.movie.service.revenue.RevenueService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/revenue")
public class AdminRevenueController {

    private final RevenueService service;
    public AdminRevenueController(RevenueService service) { this.service = service; }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Overview> overview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) Long movieId,
            @RequestParam(defaultValue = "false") boolean onlyPaid,
            @RequestParam(defaultValue = "DAY") String groupBy
    ) {
        return ApiResponse.ok(service.getOverview(from, to, cinemaId, movieId, onlyPaid, groupBy));
    }
}

