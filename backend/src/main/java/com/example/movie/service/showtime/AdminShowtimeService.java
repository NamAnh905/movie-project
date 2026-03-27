package com.example.movie.service.showtime;

import com.example.movie.dto.request.admin.AdminShowtimeRequest;
import com.example.movie.dto.response.admin.AdminShowtimeBatchResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.dto.response.shared.ShowtimeResponse;
import com.example.movie.entity.Cinema;
import com.example.movie.entity.Movie;
import com.example.movie.entity.Showtime;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.showtime.AdminShowtimeMapper;
import com.example.movie.repository.CinemaRepository;
import com.example.movie.repository.MovieRepository;
import com.example.movie.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminShowtimeService {

    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository movieRepo;
    private final CinemaRepository cinemaRepo;
    private final AdminShowtimeMapper mapper;

    @Transactional
    public AdminShowtimeBatchResponse createBatch(AdminShowtimeRequest req) {
        Movie movie = movieRepo.findById(req.movieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        Cinema cinema = cinemaRepo.findById(req.cinemaId())
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));

        BigDecimal price = req.price() != null ? req.price() : new BigDecimal("75000.00");
        int capacity = req.capacity() != null ? req.capacity() : 50;
        int duration = movie.getDuration() != null && movie.getDuration() > 0 ? movie.getDuration() : 120; // BỎ REFLECTION

        LocalDate date = LocalDate.parse(req.date());
        List<Long> createdIds = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (String timeStr : req.times()) {
            if (timeStr == null || timeStr.trim().isEmpty()) continue;

            String[] parts = timeStr.trim().split(":");
            LocalDateTime st = LocalDateTime.of(date, LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));

            boolean duplicated = showtimeRepo.findFirstByCinema_IdAndMovie_IdAndStartTimeBetween(
                            cinema.getId(), movie.getId(), st.minusMinutes(1), st.plusMinutes(1))
                    .isPresent();

            if (duplicated) {
                skipped.add(st.toLocalTime().toString());
                continue;
            }

            Showtime s = Showtime.builder()
                    .cinema(cinema)
                    .movie(movie)
                    .startTime(st)
                    .endTime(st.plusMinutes(duration))
                    .price(price)
                    .capacity(capacity)
                    .status("OPEN")
                    .build();

            createdIds.add(showtimeRepo.save(s).getId());
        }

        return new AdminShowtimeBatchResponse(createdIds, skipped);
    }

    @Transactional
    public void delete(Long id) {
        showtimeRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PageResponse<ShowtimeResponse> search(Long movieId, Long cinemaId, String state, LocalDate date, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = date != null ? date.atStartOfDay() : null;
        LocalDateTime to = date != null ? date.atTime(LocalTime.MAX) : null;

        // Trạng thái đã được đẩy xuống SQL xử lý
        Page<Showtime> page = showtimeRepo.searchAdmin(cinemaId, movieId, from, to, state, now, pageable);
        return mapper.toPageResponse(page);
    }
}