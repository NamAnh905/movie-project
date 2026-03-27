package com.example.movie.mapper.user;

import com.example.movie.dto.response.admin.AdminUserResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminUserMapper {
    AdminUserResponse toResponse(User user);

    List<AdminUserResponse> toResponseList(List<User> users);

    default PageResponse<AdminUserResponse> toPageResponse(Page<User> page) {
        if (page == null) {
            return null;
        }

        return PageResponse.<AdminUserResponse>builder()
                .currentPage(page.getNumber() + 1) // Frontend tính từ 1
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .items(toResponseList(page.getContent())) // Gọi lại hàm map list ở trên
                .build();
    }
}