package com.example.movie.controller.auth;

import com.example.movie.dto.request.auth.UpdateUserRoleRequest;
import com.example.movie.dto.response.user.UserDTO;
import com.example.movie.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService service;
    public AdminUserController(AdminUserService service){ this.service = service; }

    @GetMapping
    public Page<UserDTO> list(@RequestParam(value = "q", required = false) String q, Pageable pageable){
        return service.list(q, pageable);
    }

    @PutMapping("/{id}/role")
    public UserDTO updateRole(@PathVariable Long id, @Valid @RequestBody UpdateUserRoleRequest req){
        return service.updateRole(id, req.role());
    }
}
