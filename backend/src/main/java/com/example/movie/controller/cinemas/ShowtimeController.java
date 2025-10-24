package com.example.movie.controller.cinemas;

import com.example.movie.dto.response.showtime.ShowtimeDTO;
import com.example.movie.dto.response.showtime.ShowtimePublicDTO;
import com.example.movie.service.cinema.ShowtimeService;
import com.example.movie.dto.request.showtime.ShowtimeCreateRequest;
import com.example.movie.dto.request.showtime.ShowtimeBatchCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.movie.dto.response.showtime.ShowtimeByMovieDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {
    private final ShowtimeService service;
    public ShowtimeController(ShowtimeService service){ this.service = service; }

    // Tạo lịch chiếu
    @PostMapping
    public ShowtimeDTO create(@RequestBody ShowtimeCreateRequest req) {
        return service.create(req);
    }

    @PostMapping("/batch")
    public java.util.List<ShowtimeDTO> createBatch(@RequestBody ShowtimeBatchCreateRequest body) {
        return service.createBatch(body);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    // Tìm kiếm lịch chiếu theo trạng thái "NOW_SHOWING" | "UPCOMING" (tùy chọn),
    // theo ngày (yyyy-MM-dd, tùy chọn), movieId/cinemaId (tùy chọn)
    @GetMapping
    public Page<ShowtimeDTO> search(
            @RequestParam(required=false) Long movieId,
            @RequestParam(required=false) Long cinemaId,
            @RequestParam(required=false) String state,
            @RequestParam(required=false) String date, // yyyy-MM-dd
            Pageable pageable){
        LocalDate d = (date!=null && !date.isBlank()) ? LocalDate.parse(date) : null;
        return service.search(movieId, cinemaId, state, d, pageable);
    }

    @GetMapping("/public")
    public ResponseEntity<List<ShowtimePublicDTO>> getPublicShowtimes(
            @RequestParam Long cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getPublicShowtimes(cinemaId, date));
    }

    @GetMapping("/public/by-movie")
    public ResponseEntity<List<ShowtimeByMovieDTO>> getPublicByMovie(
            @RequestParam Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getPublicShowtimesByMovie(movieId, date));
    }

    @GetMapping("/resolve")
    public ResponseEntity<?> resolve(
            @RequestParam Long cinemaId,
            @RequestParam Long movieId,
            @RequestParam String date,
            @RequestParam String time) {
        Long id = service.resolveShowtimeId(cinemaId, movieId, date, time);
        return (id == null)
                ? ResponseEntity.status(404).body(java.util.Map.of("message","Not found"))
                : ResponseEntity.ok(java.util.Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ShowtimeDTO getOne(@PathVariable Long id) {
        return service.getOneDTO(id);
    }
}
