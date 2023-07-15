package ru.practicum.shareit.booking.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public static BookingResponseDto returnBookingDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.returnItemDto(booking.getItem()))
                .booker(UserMapper.returnUserDto(booking.getBooker()))
                .build();
    }

    public static BookingShortDto returnBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking returnBooking(BookingDto bookingDto) {
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

    public static List<BookingResponseDto> returnBookingDtoList(Iterable<Booking> bookings) {
        List<BookingResponseDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(returnBookingDto(booking));
        }
        return result;
    }
}
