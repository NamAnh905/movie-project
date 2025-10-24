package com.example.movie.dto.request.showtime;

import java.math.BigDecimal;
import java.util.List;

/** Payload mà Admin UI gửi khi tạo nhiều suất trong 1 ngày của 1 rạp-1 phim */
public class AdminCreateShowtimesRequest {
    public Long cinemaId;
    public Long movieId;
    /** yyyy-MM-dd */
    public String date;
    /** danh sách "HH:mm" */
    public List<String> times;

    /** Giá vé bắt buộc ở Admin (ví dụ 85000) */
    public BigDecimal price;

    /** Sức chứa phòng/khung giờ (ví dụ 50) */
    public Integer capacity;
}
