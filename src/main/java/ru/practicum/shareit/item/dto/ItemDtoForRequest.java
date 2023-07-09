package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoForRequest {
    Long id;
    @NotBlank(message = "Имя не может быть пустым")
    String name;
    String description;
    Boolean available;
    Long requestId;
    Long ownerId;
}
