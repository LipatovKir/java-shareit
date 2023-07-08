package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingPostDto dto, Long userId);

    Booking patchBooking(Long bookingId, Boolean approved, Long userId);

    BookingDetailedDto findById(Long bookingId, Long userId);

    List<BookingDetailedDto> findAllByBooker(String state, Long userId);

    List<BookingDetailedDto> findAllByItemOwner(String state, Long userId);
}
