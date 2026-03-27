package com.example.movie.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AdminCinemaRequest(
        @NotBlank(message = "Tên rạp không được để trống") String name,
        @NotBlank(message = "Địa chỉ không được để trống") String address,
        @NotBlank(message = "Trạng thái không được để trống") String status
) {}