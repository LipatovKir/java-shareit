package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.StatusDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    //Добавление нового запроса на бронирование.
    // Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
    // Эндпоинт — POST /bookings.
    // После создания запрос находится в статусе WAITING — «ожидает подтверждения».
    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody BookingDto bookingDto) {
        UserDto userDto = userService.findUserById(userId);
        Item item = itemService.getItemByOwner(bookingDto.getItemId());
        itemService.checkItemsAvailability(bookingDto.getItemId());
        return bookingService.addNewBooking(userDto, item, bookingDto);
    }

    //Подтверждение или отклонение запроса на бронирование.
    // Может быть выполнено только владельцем вещи.
    // Затем статус бронирования становится либо APPROVED, либо REJECTED.
    // Эндпоинт — PATCH /bookings/{bookingId}?approved={approved},
    // параметр approved может принимать значения true или false.
    @PatchMapping("/{bookingId}")
    public BookingDto patch(@PathVariable("bookingId") Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam Boolean approved) {
        return bookingService.patch(bookingId, userId, approved);
    }

    //Получение данных о конкретном бронировании (включая его статус).
    //Может быть выполнено либо автором бронирования, либо владельцем вещи,
    //к которой относится бронирование. Эндпоинт — GET /bookings/{bookingId}.
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable("bookingId") Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    //Получение списка всех бронирований текущего пользователя.
    //Эндпоинт — GET /bookings?state={state}. Параметр state необязательный и по умолчанию
    //равен ALL (англ. «все»). Также он может принимать значения CURRENT (англ. «текущие»),
    //**PAST** (англ. «завершённые»), FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»),
    //REJECTED (англ. «отклонённые»).
    //Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @GetMapping()
    public List<BookingDto> getBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") StatusDto state,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from) {
        UserDto userDto = userService.findUserById(userId);
        User user = UserMapper.dtoToUser(userDto);
        return bookingService.getBookingsByUserAndState(user, state, PageRequest.of(from / size, size));
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    //Эндпоинт — GET /bookings/owner?state={state}.
    //Этот запрос имеет смысл для владельца хотя бы одной вещи.
    //Работа параметра state аналогична его работе в предыдущем сценарии.
    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") StatusDto state,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from) {
        UserDto userDto = userService.findUserById(userId);
        User user = UserMapper.dtoToUser(userDto);
        return bookingService.getBookingsByOwnerAndState(user, state, PageRequest.of(from, size));
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(ConversionException e) {
        Map<String, String> errorMessage = new HashMap<>();
        errorMessage.put("error", "Unknown state: UNSUPPORTED_STATUS");
        return errorMessage;
    }
}
