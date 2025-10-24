package com.example.movie.dto.response.showtime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Dùng cho màn “Xem lịch chiếu của 1 phim”, gom theo rạp */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeByMovieDTO {
    private Long cinemaId;
    private String cinemaName;
    private List<String> times;   // HH:mm đã sort
}
