package com.example.movie.dto.response.showtime;

import java.util.List;

// Lombok annotations để giảm code getter/setter
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO công khai cho màn hình người dùng hiển thị lịch chiếu theo rạp + ngày
 * Dùng để trả về danh sách phim kèm ảnh và các khung giờ chiếu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimePublicDTO {

    private Long movieId;          // ID phim
    private String movieTitle;     // Tên phim
    private String posterUrl;      // URL ảnh poster phim
    private List<String> times;    // Danh sách khung giờ chiếu (HH:mm)
}
