package com.example.movie.controller.client;

import com.example.movie.dto.request.client.UserProfileRequest;
import com.example.movie.dto.response.client.UserResponse;
import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(Authentication auth) {
        return ApiResponse.success(userService.getMyProfile(auth.getName()));
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMe(
            Authentication auth,
            @Valid @RequestBody UserProfileRequest req) {
        return ApiResponse.success(userService.updateMyProfile(auth.getName(), req));
    }
}