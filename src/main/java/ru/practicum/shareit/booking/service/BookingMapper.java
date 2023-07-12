package ru.practicum.shareit.booking.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.model.User;


@UtilityClass
public class BookingMapper {


    public static BookingDto makeBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.makeUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );
    }

    public static BookingShortDto makeBookingShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getItem().getId()
        );
    }

    public static Booking makeBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user
        );
    }
}
