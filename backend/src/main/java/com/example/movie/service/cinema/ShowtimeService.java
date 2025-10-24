package com.example.movie.service.cinema;

import com.example.movie.model.movie.Movie;
import com.example.movie.repository.movie.MovieRepository;
import com.example.movie.dto.response.showtime.ShowtimeDTO;
import com.example.movie.dto.request.showtime.ShowtimeCreateRequest;
import com.example.movie.dto.request.showtime.ShowtimeBatchCreateRequest;
import com.example.movie.dto.response.showtime.ShowtimePublicDTO;
import com.example.movie.model.cinema.Showtime;
import com.example.movie.repository.cinema.CinemaRepository;
import com.example.movie.repository.cinema.ShowtimeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.movie.dto.response.showtime.ShowtimeByMovieDTO;
import com.example.movie.model.cinema.Cinema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;

import java.time.*;

@Service
public class ShowtimeService {
    private final ShowtimeRepository repo;
    private final MovieRepository movieRepo;
    private final CinemaRepository cinemaRepo;

    public ShowtimeService(ShowtimeRepository repo, MovieRepository movieRepo, CinemaRepository cinemaRepo) {
        this.repo = repo; this.movieRepo = movieRepo; this.cinemaRepo = cinemaRepo;
    }

    @Transactional
    public ShowtimeDTO create(ShowtimeCreateRequest req) {
        var movie  = movieRepo.findById(req.movieId).orElseThrow(() -> new IllegalArgumentException("Movie not found"));
        var cinema = cinemaRepo.findById(req.cinemaId).orElseThrow(() -> new IllegalArgumentException("Cinema not found"));

        var start = req.startTime;
        if (start == null) throw new IllegalArgumentException("startTime is required");

        int minutes = (movie.getDuration() != null ? movie.getDuration() : 120);
        var end = start.plusMinutes(minutes);

        var s = new Showtime();
        s.setMovie(movie);
        s.setCinema(cinema);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setPrice(req.price != null ? req.price : new java.math.BigDecimal("90000.00"));
        s.setStatus("OPEN");

        s = repo.save(s);
        return toDTO(s, java.time.ZonedDateTime.now(java.time.ZoneId.systemDefault()).toLocalDateTime());
    }

    @Transactional
    public java.util.List<ShowtimeDTO> createBatch(ShowtimeBatchCreateRequest body) {
        if (body == null || body.items == null || body.items.isEmpty()) return java.util.List.of();
        java.util.List<ShowtimeDTO> out = new java.util.ArrayList<>();
        for (var it : body.items) out.add(create(it));
        return out;
    }

    @Transactional
    public void delete(Long id) { repo.deleteById(id); }

    /**
     * state = NOW_SHOWING | UPCOMING | (null = tất cả)
     * date (yyyy-MM-dd) tùy chọn: lọc theo ngày (dải [00:00, 23:59:59])
     */
    @Transactional(readOnly = true)
    public Page<ShowtimeDTO> search(Long movieId, Long cinemaId, String state, LocalDate date, Pageable pageable) {
        LocalDateTime now = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime from = null, to = null;
        if (date != null) {
            from = date.atStartOfDay();
            to   = date.atTime(LocalTime.MAX);
        } else if ("UPCOMING".equalsIgnoreCase(state)) {
            from = now; // chỉ lấy các suất bắt đầu sau hiện tại
        }

        Page<Showtime> page = repo.search(movieId, cinemaId, from, to, pageable);
        Page<ShowtimeDTO> mapped = page.map(s -> toDTO(s, now));

        if (state == null || state.isBlank()) {
            return mapped; // không lọc thêm
        }

        String want = state.toUpperCase();
        List<ShowtimeDTO> filtered = mapped.getContent().stream()
                .filter(dto -> want.equals(dto.computedState()))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }


    private ShowtimeDTO toDTO(Showtime s, LocalDateTime now) {
        String computed = computeState(s.getStartTime(), s.getEndTime(), now);
        return new ShowtimeDTO(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getCinema().getId(),
                s.getCinema().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice(),
                s.getStatus(),
                computed
        );
    }

    private String computeState(LocalDateTime start, LocalDateTime end, LocalDateTime now){
        if ((now.isAfter(start) || now.isEqual(start)) && now.isBefore(end)) return "NOW_SHOWING";
        if (now.isBefore(start)) return "UPCOMING";
        return "ENDED";
    }

    @Transactional(readOnly = true)
    public List<ShowtimePublicDTO> getPublicShowtimes(Long cinemaId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();

        List<Showtime> showtimes = repo.findPublicWithMovie(cinemaId, start, end);

        Map<Movie, List<Showtime>> byMovie = showtimes.stream()
                .filter(s -> s.getMovie() != null)
                .collect(Collectors.groupingBy(Showtime::getMovie));

        return byMovie.entrySet().stream().map(e -> {
            Movie m = e.getKey();
            List<String> times = e.getValue().stream()
                    .map(s -> s.getStartTime().toLocalTime().toString())
                    .sorted()
                    .toList();
            return new ShowtimePublicDTO(m.getId(), m.getTitle(), m.getPosterUrl(), times);
        }).toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<ShowtimeByMovieDTO> getPublicShowtimesByMovie(Long movieId, java.time.LocalDate date) {
        java.time.LocalDateTime start = date.atStartOfDay();
        java.time.LocalDateTime end   = date.plusDays(1).atStartOfDay();

        java.util.List<Showtime> showtimes = repo.findPublicByMovieWithCinema(movieId, start, end);

        java.util.Map<Cinema, java.util.List<Showtime>> byCinema = showtimes.stream()
                .filter(s -> s.getCinema() != null)
                .collect(java.util.stream.Collectors.groupingBy(Showtime::getCinema));

        return byCinema.entrySet().stream().map(e -> {
            Cinema c = e.getKey();
            java.util.List<String> times = e.getValue().stream()
                    .map(s -> s.getStartTime().toLocalTime().toString())
                    .sorted()
                    .toList();
            return new ShowtimeByMovieDTO(c.getId(), c.getName(), times);
        }).toList();
    }

    public Long resolveShowtimeId(Long cinemaId, Long movieId, String date, String hhmm) {
        LocalDate d = LocalDate.parse(date);                          // yyyy-MM-dd
        LocalTime t = LocalTime.parse(hhmm.length()==5 ? hhmm : hhmm.substring(0,5));
        LocalDateTime at = LocalDateTime.of(d, t);
        return repo
                .findFirstByCinema_IdAndMovie_IdAndStartTimeBetween(
                        cinemaId, movieId, at.minusMinutes(1), at.plusMinutes(1))
                .map(Showtime::getId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public ShowtimeDTO getOneDTO(Long id) {
        var s = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại"));
        var now = java.time.LocalDateTime.now();
        String computed = computeState(s.getStartTime(), s.getEndTime(), now);
        return new ShowtimeDTO(
                s.getId(),
                s.getMovie().getId(),
                s.getMovie().getTitle(),
                s.getCinema().getId(),
                s.getCinema().getName(),
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice(),
                s.getStatus(),
                computed
        );
    }

}
