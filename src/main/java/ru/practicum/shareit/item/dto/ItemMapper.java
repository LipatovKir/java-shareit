package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        Long itemRequestId;
        if (item.getItemRequest() != null) {
            itemRequestId = item.getItemRequest().getId();
        } else {
            itemRequestId = null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                itemRequestId
        );
    }

    public static ItemDtoForBooking toItemDtoForBooking(Item item, List<BookingSmallDto> bookings, List<CommentResponseDto> commentResponseList) {
        ItemDtoForBooking itemDtoForBooking = new ItemDtoForBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        if ((bookings != null) && (bookings.size() >= 2)) {
            List<BookingSmallDto> bookingsOneItem = bookings.stream()
                    .filter(bookingSmallDto -> Objects.equals(bookingSmallDto.getItemId(), item.getId()))
                    .collect(Collectors.toList());

            if ((bookingsOneItem != null) && (bookingsOneItem.size() >= 2)) {
                itemDtoForBooking.setLastBooking(bookings.get(0));
                itemDtoForBooking.setNextBooking(bookings.get(1));
            }
        }
        if ((commentResponseList != null) && (commentResponseList.size() != 0)) {
            itemDtoForBooking.setComments(commentResponseList);
        } else {
            itemDtoForBooking.setComments(new ArrayList<CommentResponseDto>());
        }
        return itemDtoForBooking;
    }

    public static Item dtoToItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner()
        );
    }

    public static ItemDtoForRequest requestDtoToItem(Item item) {
        Long itemRequestId;
        if (item.getItemRequest() != null) {
            itemRequestId = item.getItemRequest().getId();
        } else {
            itemRequestId = null;
        }
        return new ItemDtoForRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                itemRequestId,
                item.getOwner().getId()
        );
    }
}

