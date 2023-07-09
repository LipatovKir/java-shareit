package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    // POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса,
    // где пользователь описывает, какая именно вещь ему нужна.
    @PostMapping
    public ResponseEntity<ItemRequestDto> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemRequestService.addNewItemRequest(userId, itemRequestDto));
    }

    // GET /requests — получить список своих запросов вместе с данными об ответах на них.
    // Для каждого запроса должны указываться описание, дата и время создания и список ответов
    // в формате: id вещи, название, id владельца. Так в дальнейшем, используя указанные id вещей,
    // можно будет получить подробную информацию о каждой вещи. Запросы должны возвращаться в
    // отсортированном порядке от более новых к более старым.
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.getUsersRequests(userId));
    }

    // GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public List<ItemRequestDto> getRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from) {
        return itemRequestService.getRequests(userId, PageRequest.of(from / size, size));
    }

    // GET /requests/{requestId} — получить данные об одном конкретном запросе (любой пользователь)
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable("requestId") Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
