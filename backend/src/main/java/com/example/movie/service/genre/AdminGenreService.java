package com.example.movie.service.genre;

import com.example.movie.dto.request.admin.AdminGenreRequest;
import com.example.movie.dto.response.shared.GenreResponse;
import com.example.movie.entity.Genre;
import com.example.movie.exception.AppException;
import com.example.movie.exception.ErrorCode;
import com.example.movie.mapper.genre.GenreMapper;
import com.example.movie.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminGenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional
    public GenreResponse create(AdminGenreRequest req) {
        if (genreRepository.existsByNameIgnoreCase(req.name())) {
            throw new AppException(ErrorCode.DATA_INVALID);
        }
        Genre genre = genreMapper.toEntity(req);
        return genreMapper.toResponse(genreRepository.save(genre));
    }

    @Transactional
    public GenreResponse update(Long id, AdminGenreRequest req) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_INVALID));

        genreMapper.updateEntityFromRequest(req, genre);
        return genreMapper.toResponse(genreRepository.save(genre));
    }

    @Transactional
    public void delete(Long id) {
        // JPA tự lo việc cascade xóa dữ liệu trong bảng phụ movie_genres nếu database đã thiết lập khóa ngoại chuẩn
        genreRepository.deleteById(id);
    }
}