package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.DetailedCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto dto, Long userId);

    DetailedCommentDto createComment(CreateCommentDto dto, Long itemId, Long userId);

    ItemDto updateItem(ItemDto dto, Long itemId, Long userId);

    ItemDto findItemById(Long itemId, Long userId);

    List<ItemDto> findAllItems(Long userId);

    List<ItemDto> findItemsByRequest(String text, Long userId);
}
