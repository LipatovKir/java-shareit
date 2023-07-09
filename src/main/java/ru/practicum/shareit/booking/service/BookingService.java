package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.enums.StatusDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(UserDto userDto, Item item, BookingDto bookingDto);

    BookingDto patch(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUserAndState(User user, StatusDto statusDto, PageRequest pageRequest);

    List<BookingDto> getBookingsByOwnerAndState(User user, StatusDto statusDto, PageRequest pageRequest);

    List<BookingSmallDto> getBookingsByItem(Long itemId);

    List<BookingSmallDto> getBookingsByOwner(UserDto userDto);
}
