package com.example.movie.service.movie;

import com.example.movie.dto.request.admin.AdminMovieRequest;
import com.example.movie.dto.response.shared.MovieResponse;
import com.example.movie.entity.Movie;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.movie.MovieMapper;
import com.example.movie.repository.GenreRepository;
import com.example.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AdminMovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieMapper movieMapper;

    @Transactional
    public MovieResponse create(AdminMovieRequest req) {
        Movie movie = new Movie();
        movieMapper.updateEntityFromRequest(req, movie);

        // Giao việc lưu bảng trung gian movie_genres cho Hibernate
        if (req.genreIds() != null && !req.genreIds().isEmpty()) {
            movie.setGenres(new HashSet<>(genreRepository.findAllById(req.genreIds())));
        }

        return movieMapper.toResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse update(Long id, AdminMovieRequest req) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND)); // Sử dụng AppException chuẩn

        movieMapper.updateEntityFromRequest(req, movie);

        if (req.genreIds() != null) {
            movie.setGenres(new HashSet<>(genreRepository.findAllById(req.genreIds())));
        }

        return movieMapper.toResponse(movieRepository.save(movie));
    }

    @Transactional
    public void delete(Long id) {
        if (!movieRepository.existsById(id)) return;
        movieRepository.deleteById(id);
    }
}