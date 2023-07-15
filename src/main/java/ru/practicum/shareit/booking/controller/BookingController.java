package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.validation_label.Create;
import ru.practicum.shareit.validation_label.Update;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    public static final String X_SHARER_USER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader(X_SHARER_USER) Long userId,
                                            @Validated({Create.class})
                                            @RequestBody
                                            @Valid BookingDto bookingDto) {
        log.info("Пользователь создал новое бронирование");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader(X_SHARER_USER) Long userId,
                                            @Validated({Update.class})
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        log.info("Пользователь {} изменил статус бронирования {}", userId, bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader(X_SHARER_USER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос бронирования {}", bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByBooker(@RequestHeader(X_SHARER_USER) Long userId,
                                                       @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Запрос всех бронирований {}", userId);
        return bookingService.getBookingByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByOwner(@RequestHeader(X_SHARER_USER) Long userId,
                                                      @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Запрос всех бронирований {}", userId);
        return bookingService.getBookingByOwner(userId, state);
    }
}
