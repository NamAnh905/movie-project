package com.example.movie.service.movie;

import com.example.movie.dto.response.shared.MovieResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.Movie;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.movie.MovieMapper;
import com.example.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> list(String q, Long genreId, String status, Pageable pageable) {
        String qNorm = (q == null || q.isBlank()) ? null : q;
        String st = (status == null || status.isBlank()) ? null : status;

        Page<Movie> page = movieRepository.search(st, genreId, qNorm, pageable);
        return movieMapper.toPageResponse(page);
    }

    @Transactional(readOnly = true)
    public MovieResponse getById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        return movieMapper.toResponse(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> findAllByStatus(String status) {
        return movieMapper.toResponseList(movieRepository.findByStatusOrderByReleaseDateDesc(status));
    }
}