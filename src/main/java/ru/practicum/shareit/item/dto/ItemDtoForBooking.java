package ru.practicum.shareit.item.dto;

import jdk.jfr.BooleanFlag;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Transient;
import ru.practicum.shareit.booking.dto.BookingShortDto;
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
    @NotBlank
    String name;
    @NotEmpty
    String description;
    @BooleanFlag()
    @NotNull
    Boolean available;
    @Transient
    User owner;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    List<CommentShortDto> comments;

    public ItemDtoForBooking(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
