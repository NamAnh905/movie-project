package com.example.movie.controller.cinemas;
import com.example.movie.common.ApiResponse;
import com.example.movie.dto.response.cinema.CinemaDTO;
import com.example.movie.service.cinema.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<?> create(@RequestBody CinemaDTO dto) {
        return ApiResponse.ok(service.create(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody CinemaDTO dto) {
        return ApiResponse.ok(service.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok("Deleted", null);
    }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(service.list(org.springframework.data.domain.PageRequest.of(page, size)));
    }

    @GetMapping("/public")
    public java.util.List<CinemaDTO> publicList() {
        return service.listPublic();
    }
}
