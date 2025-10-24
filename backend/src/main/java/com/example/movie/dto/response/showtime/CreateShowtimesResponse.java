package com.example.movie.dto.response.showtime;

import java.util.List;

/** Kết quả tạo lô: id đã tạo + các khung giờ bị bỏ qua do trùng */
public class CreateShowtimesResponse {
    public List<Long> createdIds;
    public List<String> skippedTimes;

    public CreateShowtimesResponse(List<Long> createdIds, List<String> skippedTimes) {
        this.createdIds = createdIds;
        this.skippedTimes = skippedTimes;
    }
}
