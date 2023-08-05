package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constanta.Constant.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @RequestBody
                                             @Valid ItemDto itemDto) {
        log.info("Пользователь {} добавил новую вещь {} ", userId, itemDto.getName());
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable("itemId") Long itemId) {
        log.info("Пользователь {} обновил вещь {} ", userId, itemDto.getName());
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.info("Запрос получения вещи {} ", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                  @PositiveOrZero
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive
                                                  @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Список вещей пользователя {} ", userId);
        return itemClient.getItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItem(@RequestParam("text") String text,
                                                @PositiveOrZero
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поиск вещи по символу {} ", text);
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestBody
                                             @Valid CommentDto commentDto) {
        log.info("Пользователь {} добавил комментарий к вещи {} ", userId, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}