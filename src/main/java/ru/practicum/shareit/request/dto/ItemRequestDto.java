package ru.practicum.shareit.request.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}

