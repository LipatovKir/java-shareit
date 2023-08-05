package ru.practicum.shareit.booking.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model_dto.BookingDto;
import ru.practicum.shareit.booking.model_dto.BookingResponseDto;
import ru.practicum.shareit.booking.model_dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.service.UserMapper;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public static BookingResponseDto makeBookingDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.makeItemInDto(booking.getItem()))
                .booker(UserMapper.makeUserInDto(booking.getBooker()))
                .build();
    }

    public static BookingShortDto makeBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking makeDtoInBooking(BookingDto bookingDto) {
        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
        if (bookingDto.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        } else {
            booking.setStatus(bookingDto.getStatus());
        }
        return booking;
    }

    public static List<BookingResponseDto> makeBookingDtoList(Iterable<Booking> bookings) {
        List<BookingResponseDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(makeBookingDto(booking));
        }
        return result;
    }
}