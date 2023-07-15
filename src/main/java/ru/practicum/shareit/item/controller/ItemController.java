package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    public static final String X_SHARER_USER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(X_SHARER_USER) Long userId,
                              @RequestBody
                              @Valid ItemDto itemDto) {
        log.info("Пользователь {} добавил новую вещь {}", userId, itemDto.getName());
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER) Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {

        log.info("Пользователь {} обновил вещь {}", userId, itemDto.getName());
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(X_SHARER_USER) Long userId,
                           @PathVariable Long itemId) {
        log.info("Запрос получения вещи {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsUser(@RequestHeader(X_SHARER_USER) Long userId) {
        log.info("Список вещей пользователя {}", userId);
        return itemService.getItemsUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItem(String text) {
        log.info("Поиск вещи по символу {}", text);
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody
                                 @Valid CommentDto commentDto) {

        log.info("Пользователь {} добавил комментарий к вещи {}", userId, itemId);
        return itemService.createComment(userId, itemId, commentDto);
    }
}