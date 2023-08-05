package ru.practicum.shareit.booking.model_dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponseDto {

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    UserDto booker;
    ItemDto item;
}