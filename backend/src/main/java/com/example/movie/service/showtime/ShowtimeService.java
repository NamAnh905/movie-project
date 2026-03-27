package com.example.movie.service.showtime;

import com.example.movie.dto.response.client.CinemaShowtimeResponse;
import com.example.movie.dto.response.client.MovieShowtimeResponse;
import com.example.movie.dto.response.shared.ShowtimeResponse;
import com.example.movie.entity.Cinema;
import com.example.movie.entity.Movie;
import com.example.movie.entity.Showtime;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.showtime.AdminShowtimeMapper;
import com.example.movie.repository.ShowtimeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeService {

    ShowtimeRepository showtimeRepo;
    AdminShowtimeMapper showtimeMapper;

    public List<MovieShowtimeResponse> getPublicShowtimes(Long cinemaId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Showtime> showtimes = showtimeRepo.findPublicWithMovie(cinemaId, start, end);

        Map<Movie, List<Showtime>> byMovie = showtimes.stream()
                .filter(s -> s.getMovie() != null)
                .collect(Collectors.groupingBy(Showtime::getMovie));

        return byMovie.entrySet().stream().map(e -> {
            Movie m = e.getKey();
            List<String> times = e.getValue().stream()
                    .map(s -> s.getStartTime().toLocalTime().toString())
                    .sorted()
                    .toList();
            return new MovieShowtimeResponse(m.getId(), m.getTitle(), m.getPosterUrl(), times);
        }).toList();
    }

    public List<CinemaShowtimeResponse> getPublicShowtimesByMovie(Long movieId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Showtime> showtimes = showtimeRepo.findPublicByMovieWithCinema(movieId, start, end);

        Map<Cinema, List<Showtime>> byCinema = showtimes.stream()
                .filter(s -> s.getCinema() != null)
                .collect(Collectors.groupingBy(Showtime::getCinema));

        return byCinema.entrySet().stream().map(e -> {
            Cinema c = e.getKey();
            List<String> times = e.getValue().stream()
                    .map(s -> s.getStartTime().toLocalTime().toString())
                    .sorted()
                    .toList();
            return new CinemaShowtimeResponse(c.getId(), c.getName(), times);
        }).toList();
    }

    public Long resolveShowtimeId(Long cinemaId, Long movieId, String date, String hhmm) {
        LocalDate d = LocalDate.parse(date); // yyyy-MM-dd
        LocalTime t = LocalTime.parse(hhmm.length() == 5 ? hhmm : hhmm.substring(0, 5));
        LocalDateTime at = LocalDateTime.of(d, t);

        return showtimeRepo
                .findFirstByCinema_IdAndMovie_IdAndStartTimeBetween(
                        cinemaId, movieId, at.minusMinutes(1), at.plusMinutes(1))
                .map(Showtime::getId)
                .orElse(null);
    }

    public ShowtimeResponse getOneDTO(Long id) {
        Showtime s = showtimeRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        return showtimeMapper.toResponse(s);
    }
}