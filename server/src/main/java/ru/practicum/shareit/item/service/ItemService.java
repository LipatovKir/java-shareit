package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long owner, ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsUser(long userId, Integer from, Integer size);

    List<ItemDto> searchItem(String text, Integer from, Integer size);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}