package com.example.movie.service.revenue;

import com.example.movie.dto.response.revenue.RevenueDTOs.*;
import com.example.movie.repository.revenue.RevenueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class RevenueService {

    private final RevenueRepository repo;
    public RevenueService(RevenueRepository repo) { this.repo = repo; }

    private Date atStart(LocalDate d) {
        return d == null ? null : Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Overview getOverview(LocalDate from, LocalDate to,
                                Long cinemaId, Long movieId,
                                boolean onlyPaid, String groupBy) {
        // Mặc định: 30 ngày gần nhất
        LocalDate toUse   = (to   == null) ? LocalDate.now() : to;
        LocalDate fromUse = (from == null) ? toUse.minusDays(29) : from;

        Date fromDate = atStart(fromUse);
        Date toDate   = atStart(toUse); // SQL cộng +1 day ở query

        var sum = repo.findSummary(fromDate, toDate, cinemaId, movieId, onlyPaid);
        Summary summary = new Summary(
                sum.getRevenue(),
                sum.getTickets()  == null ? 0 : sum.getTickets(),
                sum.getBookings() == null ? 0 : sum.getBookings()
        );

        var pointsRaw = "MONTH".equalsIgnoreCase(groupBy)
                ? repo.findByMonth(fromDate, toDate, cinemaId, movieId, onlyPaid)
                : repo.findByDay(fromDate, toDate, cinemaId, movieId, onlyPaid);

        var series = pointsRaw.stream().map(p ->
                new Point(
                        toLocalDate(p.getPeriod()),           // 👈 dùng helper
                        p.getRevenue(),
                        p.getTickets(),
                        p.getBookings()
                )
        ).collect(java.util.stream.Collectors.toList());

        var byCinema = repo.findByCinema(fromDate, toDate, cinemaId, movieId, onlyPaid)
                .stream().map(r -> new ByCinema(
                        r.getCinemaId(), r.getCinemaName(),
                        r.getRevenue(), r.getTickets(), r.getBookings()
                )).collect(Collectors.toList());

        var byMovie = repo.findByMovie(fromDate, toDate, cinemaId, movieId, onlyPaid)
                .stream().map(r -> new ByMovie(
                        r.getMovieId(), r.getMovieTitle(),
                        r.getRevenue(), r.getTickets(), r.getBookings()
                )).collect(Collectors.toList());

        return new Overview(summary, series, byCinema, byMovie);
    }

    private java.time.LocalDate toLocalDate(java.util.Date d) {
        if (d == null) return null;
        if (d instanceof java.sql.Date sd) return sd.toLocalDate(); // ✅ an toàn
        return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

}
