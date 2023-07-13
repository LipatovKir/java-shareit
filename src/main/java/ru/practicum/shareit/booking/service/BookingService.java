package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(UserDto userDto, Item item, BookingDto bookingDto);

    BookingDto patch(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUserAndState(User user, State statusDto);

    List<BookingDto> getBookingsByOwnerAndState(User user, State statusDto);

    List<BookingShortDto> getBookingsByOwner(UserDto userDto);
}
