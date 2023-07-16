package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.exception.UnsupportedStatusException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State validateState(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (RuntimeException e) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }
}