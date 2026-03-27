package com.example.movie.mapper.booking;

import com.example.movie.dto.response.admin.AdminBookingDetailResponse;
import com.example.movie.dto.response.admin.AdminBookingListResponse;
import com.example.movie.dto.response.shared.PageResponse;
import com.example.movie.entity.Booking;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminBookingMapper {

    default Instant map(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        // Ép kiểu về múi giờ mặc định của hệ thống (VD: Asia/Ho_Chi_Minh) rồi đổi ra Instant
        return value.atZone(ZoneId.systemDefault()).toInstant();
    }

    @Mapping(target = "showtimeId", source = "showtime.id")
    @Mapping(target = "startTime", source = "showtime.startTime")
    @Mapping(target = "cinemaId", source = "showtime.cinema.id")
    @Mapping(target = "cinemaName", source = "showtime.cinema.name")
    @Mapping(target = "movieId", source = "showtime.movie.id")
    @Mapping(target = "movieTitle", source = "showtime.movie.title")
    AdminBookingListResponse toListResponse(Booking booking);

    List<AdminBookingListResponse> toListResponseList(List<Booking> bookings);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "showtimeId", source = "showtime.id")
    @Mapping(target = "startTime", source = "showtime.startTime")
    @Mapping(target = "cinemaId", source = "showtime.cinema.id")
    @Mapping(target = "cinemaName", source = "showtime.cinema.name")
    @Mapping(target = "movieId", source = "showtime.movie.id")
    @Mapping(target = "movieTitle", source = "showtime.movie.title")
    @Mapping(target = "showtimePrice", source = "showtime.price")
    @Mapping(target = "timeline", ignore = true)
    AdminBookingDetailResponse toDetailResponse(Booking booking);

    // Xử lý Timeline trực tiếp trên BUILDER của Record
    @AfterMapping
    default void buildTimeline(Booking b, @MappingTarget AdminBookingDetailResponse.AdminBookingDetailResponseBuilder builder) {
        List<AdminBookingDetailResponse.BookingEvent> events = new ArrayList<>();

        if (b.getCreatedAt() != null) {
            events.add(new AdminBookingDetailResponse.BookingEvent("CREATED", b.getCreatedAt(), "Đơn được tạo"));
        }
        if ("CONFIRMED".equalsIgnoreCase(b.getStatus()) || "PAID".equalsIgnoreCase(b.getStatus())) {
            events.add(new AdminBookingDetailResponse.BookingEvent("CONFIRMED", b.getCreatedAt(), "Xác nhận giữ ghế"));
        }
        if (b.getPaidAt() != null) {
            String method = b.getPaymentMethod() != null ? b.getPaymentMethod() : "Thanh toán";
            events.add(new AdminBookingDetailResponse.BookingEvent("PAID", b.getPaidAt(), method));
        }
        if ("FAILED".equalsIgnoreCase(b.getStatus())) {
            Instant failTime = b.getPaidAt() != null ? b.getPaidAt() : b.getCreatedAt();
            events.add(new AdminBookingDetailResponse.BookingEvent("FAILED", failTime, "Thanh toán thất bại"));
        }
        if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
            events.add(new AdminBookingDetailResponse.BookingEvent("CANCELLED", b.getCreatedAt(), "Đã hủy đơn"));
        }
        builder.timeline(events);
    }

    // Gom logic Page vào Mapper (Service siêu gầy)
    default PageResponse<AdminBookingListResponse> toPageResponse(Page<Booking> page) {
        if (page == null) return null;
        return PageResponse.<AdminBookingListResponse>builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .items(toListResponseList(page.getContent()))
                .build();
    }
}