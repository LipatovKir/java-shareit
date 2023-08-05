package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constanta.Constanta.X_SHARER_USER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(X_SHARER_USER) Long userId,
                                              @RequestBody
                                              @Valid ItemDto itemDto) {
        log.info("Пользователь {} добавил новую вещь {} ", userId, itemDto.getName());
        return ResponseEntity.ok(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(X_SHARER_USER) Long userId,
                                              @RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId) {
        log.info("Пользователь {} обновил вещь {} ", userId, itemDto.getName());
        return ResponseEntity.ok(itemService.updateItem(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(X_SHARER_USER) Long userId,
                                           @PathVariable Long itemId) {
        log.info("Запрос получения вещи {} ", itemId);
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsUser(@RequestHeader(X_SHARER_USER) Long userId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Список вещей пользователя {} ", userId);
        return ResponseEntity.ok(itemService.getItemsUser(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getSearchItem(@RequestParam String text,
                                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size) {

        log.info("Поиск вещи по символу {} ", text);
        return ResponseEntity.ok(itemService.searchItem(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(X_SHARER_USER) Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody
                                                 @Valid CommentDto commentDto) {
        log.info("Пользователь {} добавил комментарий к вещи {} ", userId, itemId);
        return ResponseEntity.ok(itemService.createComment(userId, itemId, commentDto));
    }
}