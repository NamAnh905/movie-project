package com.example.movie.mapper.showtime;

import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.dto.response.shared.ShowtimeResponse;
import com.example.movie.entity.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminShowtimeMapper {

    default ShowtimeResponse toResponse(Showtime s) {
        if (s == null) return null;
        LocalDateTime now = LocalDateTime.now();
        String computedState = "ENDED";
        if ((now.isAfter(s.getStartTime()) || now.isEqual(s.getStartTime())) && now.isBefore(s.getEndTime())) {
            computedState = "NOW_SHOWING";
        } else if (now.isBefore(s.getStartTime())) {
            computedState = "UPCOMING";
        }

        return new ShowtimeResponse(
                s.getId(),
                s.getMovie() != null ? s.getMovie().getId() : null,
                s.getMovie() != null ? s.getMovie().getTitle() : null,
                s.getCinema() != null ? s.getCinema().getId() : null,
                s.getCinema() != null ? s.getCinema().getName() : null,
                s.getStartTime(),
                s.getEndTime(),
                s.getPrice(),
                s.getStatus(),
                computedState
        );
    }

    List<ShowtimeResponse> toResponseList(List<Showtime> showtimes);

    default PageResponse<ShowtimeResponse> toPageResponse(Page<Showtime> page) {
        if (page == null) return null;
        return PageResponse.<ShowtimeResponse>builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .items(toResponseList(page.getContent()))
                .build();
    }
}