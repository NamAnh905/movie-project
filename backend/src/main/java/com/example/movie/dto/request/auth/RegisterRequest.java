package com.example.movie.dto.request.auth;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Tên đăng nhập là bắt buộc")
        @Size(min = 4, max = 30, message = "Tên đăng nhập phải 4-30 ký tự")
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._-]*$",
                message = "Tên đăng nhập phải bắt đầu bằng chữ, chỉ gồm chữ/số/._-")
        String username,

        @NotBlank(message = "Mật khẩu là bắt buộc")
        @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
        String password,

        @NotBlank(message = "Email là bắt buộc")
        @Email(message = "Email không hợp lệ")
        @Size(max = 255, message = "Email tối đa 255 ký tự")
        String email,

        @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
        String fullName
) {}
