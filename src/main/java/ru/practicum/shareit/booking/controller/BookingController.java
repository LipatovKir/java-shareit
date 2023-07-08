package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation_label.Create;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    public static final String DEFAULT_STATE_VALUE = "ALL";
    public static final String X_SHARER_USER = "X-Sharer-User-Id";

    private BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody @Validated(Create.class) BookingPostDto dto,
                                 @RequestHeader(X_SHARER_USER) Long userId) {
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking patchBooking(@PathVariable Long bookingId,
                                @RequestParam Boolean approved,
                                @RequestHeader(X_SHARER_USER) Long userId) {
        return bookingService.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDetailedDto findById(@PathVariable Long bookingId,
                                       @RequestHeader(X_SHARER_USER) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDetailedDto> findAllBookings(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                                    @RequestHeader(X_SHARER_USER) Long userId) {
        return bookingService.findAllByBooker(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDetailedDto> findAll(@RequestParam(defaultValue = DEFAULT_STATE_VALUE) String state,
                                            @RequestHeader(X_SHARER_USER) Long userId) {
        return bookingService.findAllByItemOwner(state, userId);
    }
}

