package ru.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Transient;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ItemDtoForBooking {
    Long id;
    @NotBlank(message = "Имя не может быть пустым")
    String name;
    @NotEmpty(message = "Описание не может быть пустым")
    String description;
    //статус о том, доступна или нет вещь для аренды
    @BooleanFlag()
    @NotNull
    Boolean available;
    //владелец вещи
    @Transient
    User owner;
    private BookingSmallDto lastBooking;
    private BookingSmallDto nextBooking;
    List<CommentResponseDto> comments;

    public ItemDtoForBooking(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
