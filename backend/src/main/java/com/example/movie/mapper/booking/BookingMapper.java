package com.example.movie.mapper.booking;

import com.example.movie.dto.response.client.BookingResponse;
import com.example.movie.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "movieTitle", source = "showtime.movie.title")
    @Mapping(target = "cinemaName", source = "showtime.cinema.name")
    @Mapping(target = "startTime", source = "showtime.startTime")
    @Mapping(target = "remainingSeats", ignore = true) // Sẽ set tay lúc create
    BookingResponse toBookingResponse(Booking booking);
}
