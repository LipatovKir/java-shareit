package ru.practicum.shareit.booking.model_dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {

    Long itemId;
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;
}