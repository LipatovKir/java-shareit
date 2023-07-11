package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemMapper {

    ItemMapper() {
        throw new UnsupportedOperationException();
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoForBooking toItemDtoForBooking(Item item, List<BookingShortDto> bookings, List<CommentShortDto> commentResponseList) {
        ItemDtoForBooking itemDtoForBooking = new ItemDtoForBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        if ((bookings != null) && (bookings.size() >= 2)) {
            List<BookingShortDto> bookingsOneItem = bookings.stream()
                    .filter(bookingSmallDto -> Objects.equals(bookingSmallDto.getItemId(), item.getId()))
                    .collect(Collectors.toList());

            if (bookingsOneItem.size() >= 2) {
                itemDtoForBooking.setLastBooking(bookings.get(0));
                itemDtoForBooking.setNextBooking(bookings.get(1));
            }
        }
        if ((commentResponseList != null) && (commentResponseList.size() != 0)) {
            itemDtoForBooking.setComments(commentResponseList);
        } else {
            itemDtoForBooking.setComments(new ArrayList<>());
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
}

