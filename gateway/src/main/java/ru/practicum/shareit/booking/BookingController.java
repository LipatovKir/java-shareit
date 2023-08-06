package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constanta.Constant.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestBody
                                                @Valid BookingDto bookingDto) {
        log.info("Пользователь {},создал новое бронирование вещи ", userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Пользователь {} изменил статус бронирования {} ", userId, bookingId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Запрос бронирования {} ", bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBookerId(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                           @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                           @PositiveOrZero
                                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @Positive
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info("Запрос всех бронирований пользователя {} ", userId);
        return bookingClient.getAllBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForAllItemsByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                                     @PositiveOrZero
                                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                     @Positive
                                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info("Запрос всех бронирований владельца вещей {} ", userId);
        return bookingClient.getAllBookingsForAllItemsByOwnerId(userId, state, from, size);
    }
}