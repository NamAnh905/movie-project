package com.example.movie.controller.admin;

import com.example.movie.dto.request.admin.AdminUserRoleRequest;
import com.example.movie.dto.response.admin.AdminUserResponse;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.service.user.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<PageResponse<AdminUserResponse>> listUsers(
            @RequestParam(value = "q", required = false) String q,
            Pageable pageable) {
        return ApiResponse.success(adminUserService.listUsers(q, pageable));
    }

    @PutMapping("/{id}/role")
    public ApiResponse<AdminUserResponse> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRoleRequest req) {
        return ApiResponse.success(adminUserService.updateUserRole(id, req.role()));
    }
}