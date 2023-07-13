package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation_label.Create;
import ru.practicum.shareit.validation_label.Update;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String X_SHARER_USER = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(X_SHARER_USER) Long userId,
                              @Valid
                              @Validated({Create.class})
                              @RequestBody ItemDto itemDto) {
        UserDto userDto = userService.findUserById(userId);
        return itemService.addNewItem(userDto, itemDto, itemDto.getRequestId());
    }

    @PostMapping("/{itemId}/comment")
    public CommentShortDto createComment(@RequestHeader(X_SHARER_USER) Long userId,
                                         @Validated({Create.class})
                                         @PathVariable Long itemId,
                                         @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto.getText());
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemDto,
                              @Validated({Update.class})
                              @RequestHeader(X_SHARER_USER) Long userId) {
        return itemService.putItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForBooking getItemById(@PathVariable("itemId") Long itemId,
                                         @RequestHeader(X_SHARER_USER) Long userId) {
        UserDto userDto = userService.findUserById(userId);
        User user = UserMapper.makeDtoToUser(userDto);
        List<CommentShortDto> commentsResponseDto;
        commentsResponseDto = itemService.getCommentList(itemId);
        return itemService.getItemById(itemId, user, commentsResponseDto);
    }

    @GetMapping
    public List<ItemDtoForBooking> findItemsByUser(@RequestHeader(X_SHARER_USER) Long userId) {
        UserDto userDto = userService.findUserById(userId);
        List<BookingShortDto> bookings = bookingService.getBookingsByOwner(userDto);
        List<CommentShortDto> comments = itemService.getCommentList(userId);
        return itemService.getItemsByUser(userDto, bookings, comments);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(X_SHARER_USER) Long userId,
                                @RequestParam String text) {
        return itemService.search(userId, text);
    }
}
