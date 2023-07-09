package ru.practicum.shareit.request.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;
    //текст запроса, содержащий описание требуемой вещи
    @NotBlank
    String description;
    //пользователь, создавший запрос
    Long requesterId;
    LocalDateTime created;
    List<ItemDtoForRequest> items;

    public ItemRequestDto(String description) {
        this.description = description;
    }
}

