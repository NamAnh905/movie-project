package com.example.movie.service.cinema;

import com.example.movie.dto.request.showtime.AdminCreateShowtimesRequest;
import com.example.movie.dto.response.showtime.CreateShowtimesResponse;
import com.example.movie.model.cinema.Cinema;
import com.example.movie.model.cinema.Showtime;
import com.example.movie.model.movie.Movie;
import com.example.movie.repository.cinema.CinemaRepository;
import com.example.movie.repository.cinema.ShowtimeRepository;
import com.example.movie.repository.movie.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminShowtimeService {

    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository movieRepo;
    private final CinemaRepository cinemaRepo;

    public AdminShowtimeService(ShowtimeRepository showtimeRepo,
                                MovieRepository movieRepo,
                                CinemaRepository cinemaRepo) {
        this.showtimeRepo = showtimeRepo;
        this.movieRepo = movieRepo;
        this.cinemaRepo = cinemaRepo;
    }

    @Transactional
    public CreateShowtimesResponse createBatch(AdminCreateShowtimesRequest req) {
        if (req == null || req.cinemaId == null || req.movieId == null
                || req.date == null || req.times == null || req.times.isEmpty()) {
            throw new IllegalArgumentException("Thiếu cinemaId, movieId, date hoặc times");
        }

        BigDecimal price = (req.price != null ? req.price : new BigDecimal("75000.00"));
        int capacity = (req.capacity != null ? req.capacity : 50);

        Movie movie = movieRepo.findById(req.movieId)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại"));
        Cinema cinema = cinemaRepo.findById(req.cinemaId)
                .orElseThrow(() -> new IllegalArgumentException("Rạp không tồn tại"));

        LocalDate d = LocalDate.parse(req.date); // yyyy-MM-dd
        List<LocalDateTime> startTimes = req.times.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    String[] p = s.split(":");
                    int hh = Integer.parseInt(p[0]);
                    int mm = Integer.parseInt(p[1]);
                    return LocalDateTime.of(d, LocalTime.of(hh, mm));
                })
                .toList();

        List<Long> createdIds = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (LocalDateTime st : startTimes) {
            // ✅ kiểm trùng bằng method có sẵn trong Repository: findFirstBy...Between
            boolean duplicated = showtimeRepo
                    .findFirstByCinema_IdAndMovie_IdAndStartTimeBetween(
                            req.cinemaId, req.movieId,
                            st.minusMinutes(1), st.plusMinutes(1))
                    .isPresent();

            if (duplicated) {
                skipped.add(st.toLocalTime().toString()); // "HH:mm:ss"
                continue;
            }

            Showtime s = new Showtime();
            s.setCinema(cinema);
            s.setMovie(movie);
            s.setStartTime(st);

            // Tính endTime theo duration nếu có, mặc định +120'
            int duration = 120;
            try {
                var m = Movie.class.getMethod("getDuration").invoke(movie);
                if (m instanceof Integer dm && dm > 0) duration = dm;
            } catch (Exception ignored) {}
            s.setEndTime(st.plusMinutes(duration));

            s.setStatus("OPEN");
            s.setPrice(price);
            s.setCapacity(capacity);

            createdIds.add(showtimeRepo.save(s).getId());
        }

        return new CreateShowtimesResponse(createdIds, skipped);
    }

}
