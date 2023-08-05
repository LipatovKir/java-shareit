package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model_dto.BookingDto;
import ru.practicum.shareit.booking.model_dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingDto bookingDto, long userId);

    BookingResponseDto updateBooking(long userId, long bookingId, Boolean approved);

    BookingResponseDto getBookingById(long userId, long bookingId);

    List<BookingResponseDto> getBookingByBooker(long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getBookingByOwner(long userId, String state, Integer from, Integer size);
}