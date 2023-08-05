package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingDto bookingDto, long userId);

    BookingResponseDto updateBooking(long userId, long bookingId, Boolean approved);

    BookingResponseDto getBookingById(long userId, long bookingId);

    List<BookingResponseDto> getBookingByBooker(long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getBookingByOwner(long userId, String state, Integer from, Integer size);
}