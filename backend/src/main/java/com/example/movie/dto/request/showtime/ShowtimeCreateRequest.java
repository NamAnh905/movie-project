package com.example.movie.dto.request.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShowtimeCreateRequest {
    public Long movieId;
    public Long cinemaId;
    public LocalDateTime startTime;   // FE gửi yyyy-MM-ddTHH:mm:ss
    public BigDecimal price;          // optional
}
