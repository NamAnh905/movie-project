package com.example.movie.controller.auth; // Đổi package cho hợp lý nếu đây là API của Admin

import com.example.movie.dto.response.shared.ApiResponse;
import com.example.movie.service.shared.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/admin/files")
@RequiredArgsConstructor
public class FileController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File không hợp lệ (bị trống hoặc không phải định dạng ảnh)");
        }
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            return ApiResponse.success(Map.of("url", imageUrl));
        } catch (IOException e) {
            throw new RuntimeException("Có lỗi xảy ra khi đẩy ảnh lên Cloudinary", e);
        }
    }
}