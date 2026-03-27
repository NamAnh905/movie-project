package com.example.movie.service.revenue;

import com.example.movie.dto.response.admin.AdminRevenueResponse;
import com.example.movie.dto.response.admin.AdminRevenueResponse.*;
import com.example.movie.repository.AdminRevenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRevenueService {

    private final AdminRevenueRepository repo;

    private Date atStart(LocalDate d) {
        return d == null ? null : Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public AdminRevenueResponse getOverview(LocalDate from, LocalDate to,
                                            Long cinemaId, Long movieId,
                                            boolean onlyPaid, String groupBy) {
        // Mặc định: 30 ngày gần nhất
        LocalDate toUse   = (to   == null) ? LocalDate.now() : to;
        LocalDate fromUse = (from == null) ? toUse.minusDays(29) : from;

        Date fromDate = atStart(fromUse);
        Date toDate   = atStart(toUse); // SQL cộng +1 day ở query

        var sum = repo.findSummary(fromDate, toDate, cinemaId, movieId, onlyPaid);
        SummaryResponse summary = SummaryResponse.builder()
                .revenue(sum.getRevenue())
                .tickets(sum.getTickets() == null ? 0L : sum.getTickets())
                .bookings(sum.getBookings())
                .build();

        var pointsRaw = "month".equalsIgnoreCase(groupBy)
                ? repo.findByMonth(fromDate, toDate, cinemaId, movieId, onlyPaid)
                : repo.findByDay(fromDate, toDate, cinemaId, movieId, onlyPaid);

        var series = pointsRaw.stream().map(p ->
                PointResponse.builder()
                        .date(toLocalDate(p.getPeriod()))
                        .revenue(p.getRevenue())
                        .tickets(p.getTickets())
                        .bookings(p.getBookings())
                        .build()
        ).toList();

        var byCinema = repo.findByCinema(fromDate, toDate, cinemaId, movieId, onlyPaid)
                .stream().map(r -> ByCinemaResponse.builder()
                        .cinemaId(r.getCinemaId())
                        .cinemaName(r.getCinemaName())
                        .revenue(r.getRevenue())
                        .tickets(r.getTickets())
                        .bookings(r.getBookings())
                        .build()
                ).toList();

        var byMovie = repo.findByMovie(fromDate, toDate, cinemaId, movieId, onlyPaid)
                .stream().map(r -> ByMovieResponse.builder()
                        .movieId(r.getMovieId())
                        .movieTitle(r.getMovieTitle())
                        .revenue(r.getRevenue())
                        .tickets(r.getTickets())
                        .bookings(r.getBookings())
                        .build()
                ).toList();

        return AdminRevenueResponse.builder()
                .summary(summary)
                .series(series)
                .byCinema(byCinema)
                .byMovie(byMovie)
                .build();
    }

    private LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        if (d instanceof java.sql.Date sd) return sd.toLocalDate();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}