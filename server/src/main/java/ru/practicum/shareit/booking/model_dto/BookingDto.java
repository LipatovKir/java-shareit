package ru.practicum.shareit.booking.model_dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    BookingStatus status;
}