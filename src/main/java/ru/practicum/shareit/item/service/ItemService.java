package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(UserDto userDto, ItemDto itemDto, Long requestId);

    ItemDto putItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDtoForBooking getItemById(Long itemId, User user, List<CommentShortDto> commentsResponseDto);

    List<ItemDtoForBooking> getItemsByUser(UserDto userDto, List<BookingShortDto> bookings, PageRequest pageRequest);

    List<ItemDto> search(Long userId, String text, PageRequest pageRequest);

    void checkItemsAvailability(Long id);

    Object getItemByOwner(Long itemId);

    CommentShortDto createComment(Long userId, Long itemId, String text);

    List<CommentShortDto> getCommentList(Long itemId);
}
