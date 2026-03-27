package com.example.movie.controller.auth;

import com.example.movie.dto.request.auth.AuthRequest;
import com.example.movie.dto.request.auth.RegisterRequest;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.dto.response.shared.AuthResponse;
import com.example.movie.service.auth.AuthService;
import com.example.movie.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.login(req))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestParam("token") String refreshToken) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.refreshToken(refreshToken))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            authService.logout(accessToken);
        }
        return ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest dto) {
        var u = userService.register(dto);
        return ApiResponse.builder()
                .result(Map.of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "email", u.getEmail()
                ))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<?> me(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();

        return ApiResponse.builder()
                .result(Map.of(
                        "username", user.getUsername(),
                        "authorities", roles
                ))
                .build();
    }
}