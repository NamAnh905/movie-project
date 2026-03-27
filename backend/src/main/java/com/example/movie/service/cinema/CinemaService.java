package com.example.movie.service.cinema;

import com.example.movie.dto.response.shared.CinemaResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.cinema.CinemaMapper;
import com.example.movie.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    public PageResponse<CinemaResponse> list(String q, Pageable pageable) {
        String qNorm = (q == null || q.isBlank()) ? null : q;
        return cinemaMapper.toPageResponse(cinemaRepository.search(qNorm, pageable));
    }

    public List<CinemaResponse> listPublic() {
        return cinemaMapper.toResponseList(cinemaRepository.findByStatus("ACTIVE"));
    }

    public CinemaResponse getById(Long id) {
        return cinemaMapper.toResponse(cinemaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND)));
    }
}