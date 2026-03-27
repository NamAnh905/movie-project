package com.example.movie.service.cinema;

import com.example.movie.dto.request.admin.AdminCinemaRequest;
import com.example.movie.dto.response.shared.CinemaResponse;
import com.example.movie.entity.Cinema;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.cinema.CinemaMapper;
import com.example.movie.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    @Transactional
    public CinemaResponse create(AdminCinemaRequest req) {
        if (cinemaRepository.existsByNameIgnoreCase(req.name())) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        Cinema cinema = cinemaMapper.toEntity(req);
        return cinemaMapper.toResponse(cinemaRepository.save(cinema));
    }

    @Transactional
    public CinemaResponse update(Long id, AdminCinemaRequest req) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));

        if (!cinema.getName().equalsIgnoreCase(req.name()) && cinemaRepository.existsByNameIgnoreCase(req.name())) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }

        cinemaMapper.updateEntityFromRequest(req, cinema);
        return cinemaMapper.toResponse(cinemaRepository.save(cinema));
    }

    @Transactional
    public void delete(Long id) {
        // Hãy đảm bảo bạn đã xử lý Cascade ở DB hoặc ở các bảng liên quan (Showtime)
        cinemaRepository.deleteById(id);
    }
}