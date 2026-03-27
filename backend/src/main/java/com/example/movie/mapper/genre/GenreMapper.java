package com.example.movie.mapper.genre;

import com.example.movie.dto.request.admin.AdminGenreRequest;
import com.example.movie.dto.response.shared.GenreResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.Genre;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.text.Normalizer;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    GenreResponse toResponse(Genre genre);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Genre toEntity(AdminGenreRequest req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(AdminGenreRequest req, @MappingTarget Genre genre);

    List<GenreResponse> toResponseList(List<Genre> genres);

    default PageResponse<GenreResponse> toPageResponse(Page<Genre> page) {
        if (page == null) return null;
        return PageResponse.<GenreResponse>builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .items(toResponseList(page.getContent()))
                .build();
    }

    @AfterMapping
    default void handleSlug(AdminGenreRequest req, @MappingTarget Genre genre) {
        if (req.slug() == null || req.slug().isBlank()) {
            genre.setSlug(slugify(req.name()));
        } else {
            genre.setSlug(slugify(req.slug()));
        }
    }

    default String slugify(String input) {
        if (input == null) return null;
        String nfd = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return nfd.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}