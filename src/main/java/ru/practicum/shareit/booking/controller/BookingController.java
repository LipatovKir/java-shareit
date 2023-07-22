package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constanta.Constanta.X_SHARER_USER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestHeader(X_SHARER_USER) Long userId,
                                                            @RequestBody
                                                            @Valid BookingDto bookingDto) {
        log.info("Пользователь {},создал новое бронирование вещи {}", userId, bookingDto.getItemId());
        return ResponseEntity.ok(bookingService.createBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(@RequestHeader(X_SHARER_USER) Long userId,
                                                            @PathVariable Long bookingId,
                                                            @RequestParam Boolean approved) {
        log.info("Пользователь {} изменил статус бронирования {}", userId, bookingId);
        return ResponseEntity.ok(bookingService.updateBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@RequestHeader(X_SHARER_USER) Long userId,
                                                             @PathVariable Long bookingId) {
        log.info("Запрос бронирования {}", bookingId);
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingByBooker(@RequestHeader(X_SHARER_USER) Long userId,
                                                                       @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                       @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                       @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Запрос всех бронирований {}", userId);
        return ResponseEntity.ok(bookingService.getBookingByBooker(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getBookingByOwner(@RequestHeader(X_SHARER_USER) Long userId,
                                                                      @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Запрос всех бронирований {}", userId);
        return ResponseEntity.ok(bookingService.getBookingByOwner(userId, state, from, size));
    }
}