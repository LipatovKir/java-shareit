package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model_dto.BookingDto;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    @Positive
    Long requestId;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}