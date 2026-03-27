package com.example.movie.mapper.movie;

import com.example.movie.dto.request.admin.AdminMovieRequest;
import com.example.movie.dto.response.shared.MovieResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.Genre;
import com.example.movie.entity.Movie;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovieMapper {

    @Mapping(target = "genreIds", ignore = true)
    @Mapping(target = "genreNames", ignore = true)
    MovieResponse toResponse(Movie movie);

    // Tự động trích xuất ID và Tên thể loại từ Set<Genre> của Entity
    @AfterMapping
    default void mapGenres(Movie movie, @MappingTarget MovieResponse.MovieResponseBuilder dtoBuilder) {
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            dtoBuilder.genreIds(movie.getGenres().stream().map(Genre::getId).toList());
            dtoBuilder.genreNames(movie.getGenres().stream().map(Genre::getName).toList());
        }
    }

    List<MovieResponse> toResponseList(List<Movie> movies);

    default PageResponse<MovieResponse> toPageResponse(Page<Movie> page) {
        if (page == null) return null;
        return PageResponse.<MovieResponse>builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .items(toResponseList(page.getContent()))
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genres", ignore = true)
    void updateEntityFromRequest(AdminMovieRequest req, @MappingTarget Movie movie);
}