package ru.practicum.shareit.errorhandlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String error;
    private final String description;
}
