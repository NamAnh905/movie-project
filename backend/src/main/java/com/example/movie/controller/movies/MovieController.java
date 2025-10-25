package com.example.movie.controller.movies;

import com.example.movie.dto.response.movie.MovieDTO;
import com.example.movie.dto.response.PageResponse;
import com.example.movie.dto.response.movie.UpsertMovie;
import com.example.movie.service.movie.MovieService;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    /**
     * Danh sách phim + tìm kiếm + lọc theo thể loại.
     * Query params:
     *  - q: tìm theo tên (optional)
     *  - genreId: lọc theo thể loại (optional)
     *  - page, size: phân trang
     */
    @GetMapping
    public PageResponse<MovieDTO> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var p = service.list(q, genreId, PageRequest.of(page, size));
        return PageResponse.of(p);
    }

    @GetMapping("/all")
    public PageResponse<MovieDTO> listAllAlias(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var p = service.list(null, null, status, PageRequest.of(page, size));
        return PageResponse.of(p);
    }

    /** Lấy chi tiết 1 phim theo id. */
    @GetMapping("/{id}")
    public MovieDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /** Tạo mới phim. */
    @PostMapping
    public MovieDTO create(@RequestBody @Valid UpsertMovie req) {
        return service.create(req);
    }

    /** Cập nhật phim. */
    @PutMapping("/{id}")
    public MovieDTO update(@PathVariable Long id, @RequestBody @Valid UpsertMovie req) {
        return service.update(id, req);
    }

    /** Xoá phim. */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // Thêm endpoint không phân trang
    @GetMapping("/status/{status}/all")
    public  List<MovieDTO> listAllByStatus(@PathVariable String status) {
        return service.findAllByStatus(status);
    }

}

