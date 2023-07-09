package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );
    }

    public static BookingSmallDto toBookingSmallDto(Booking booking) {
        return new BookingSmallDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getItem().getId()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user
        );
    }
}
