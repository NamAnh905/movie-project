package com.example.movie.controller.cinemas;

import com.example.movie.dto.request.showtime.AdminCreateShowtimesRequest;
import com.example.movie.dto.response.showtime.CreateShowtimesResponse;
import com.example.movie.service.cinema.AdminShowtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/showtimes")
public class AdminShowtimeController {

    private final AdminShowtimeService service;

    public AdminShowtimeController(AdminShowtimeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateShowtimesResponse> create(@RequestBody AdminCreateShowtimesRequest req) {
        return ResponseEntity.ok(service.createBatch(req));
    }
}
