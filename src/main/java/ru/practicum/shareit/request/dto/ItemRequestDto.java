package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class ItemRequestDto {

    @Positive
    private long id;
    @NotNull
    @NotBlank
    @Size
    private String description;
    @Positive
    @NotNull
    private long requestor;
    @NotNull
    @PastOrPresent
    private LocalDate created;
}

