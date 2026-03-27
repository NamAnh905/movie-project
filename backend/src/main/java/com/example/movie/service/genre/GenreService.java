package com.example.movie.service.genre;

import com.example.movie.dto.response.shared.GenreResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.genre.GenreMapper;
import com.example.movie.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public PageResponse<GenreResponse> list(String q, Pageable pageable) {
        String qNorm = (q == null || q.isBlank()) ? null : q;
        return genreMapper.toPageResponse(genreRepository.search(qNorm, pageable));
    }

    public List<GenreResponse> listAll() {
        return genreMapper.toResponseList(genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
    }

    public GenreResponse getById(Long id) {
        return genreMapper.toResponse(genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_INVALID)));
    }
}