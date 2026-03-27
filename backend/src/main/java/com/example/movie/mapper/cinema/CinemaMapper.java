package com.example.movie.mapper.cinema;

import com.example.movie.dto.request.admin.AdminCinemaRequest;
import com.example.movie.dto.response.shared.CinemaResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.Cinema;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CinemaMapper {

    CinemaResponse toResponse(Cinema cinema);

    @Mapping(target = "id", ignore = true)
    Cinema toEntity(AdminCinemaRequest req);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(AdminCinemaRequest req, @MappingTarget Cinema cinema);

    List<CinemaResponse> toResponseList(List<Cinema> cinemas);

    default PageResponse<CinemaResponse> toPageResponse(Page<Cinema> page) {
        if (page == null) return null;
        return PageResponse.<CinemaResponse>builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .items(toResponseList(page.getContent()))
                .build();
    }
}