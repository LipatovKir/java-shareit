package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation_label.Create;
import ru.practicum.shareit.validation_label.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private static final String X_SHARER_USER = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(X_SHARER_USER) Long userId,
                                 @Valid
                                 @Validated({Create.class})
                                 @RequestBody BookingDto bookingDto) {
        UserDto userDto = userService.findUserById(userId);
        Item item = (Item) itemService.getItemByOwner(bookingDto.getItemId());
        itemService.checkItemsAvailability(bookingDto.getItemId());
        return bookingService.addBooking(userDto, item, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable("bookingId") Long bookingId,
                                    @RequestHeader(X_SHARER_USER) Long userId,
                                    @Validated({Update.class})
                                    @RequestParam Boolean approved) {
        return bookingService.patch(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@PathVariable("bookingId") Long bookingId,
                                      @RequestHeader(X_SHARER_USER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> findBookingsByUser(
            @RequestHeader(X_SHARER_USER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from) {
        UserDto userDto = userService.findUserById(userId);
        User user = UserMapper.makeDtoToUser(userDto);
        return bookingService.getBookingsByUserAndState(user, state, PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByOwner(
            @RequestHeader(X_SHARER_USER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") State state,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from) {
        UserDto userDto = userService.findUserById(userId);
        User user = UserMapper.makeDtoToUser(userDto);
        return bookingService.getBookingsByOwnerAndState(user, state, PageRequest.of(from, size));
    }
}
