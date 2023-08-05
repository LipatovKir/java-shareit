package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constanta.Constant.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {


    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestBody
                                                @Valid ItemRequestDto itemRequestDto) {
        log.info("Пользователь {}, создал новый запрос ", userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Получение запросов пользователя {} ", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @PositiveOrZero
                                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive
                                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение запросов всех пользователей приложения.");
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        log.info("Получение запроса по номеру {} ", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}