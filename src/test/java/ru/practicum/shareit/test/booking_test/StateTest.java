package ru.practicum.shareit.test.booking_test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.exception.UnsupportedStatusException;
import ru.practicum.shareit.booking.model.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class StateTest {
    @Test
    void validateState() {

        String state = "Unknown";
        String finalState = state;
        assertThrows(UnsupportedStatusException.class, () -> State.validateState(finalState));
        state = "ALL";
        State stateTest = State.validateState(state);
        assertEquals(State.ALL, stateTest);
        state = "CURRENT";
        stateTest = State.validateState(state);
        assertEquals(State.CURRENT, stateTest);
        state = "PAST";
        stateTest = State.validateState(state);
        assertEquals(State.PAST, stateTest);
        state = "FUTURE";
        stateTest = State.validateState(state);
        assertEquals(State.FUTURE, stateTest);
        state = "REJECTED";
        stateTest = State.validateState(state);
        assertEquals(State.REJECTED, stateTest);
        state = "WAITING";
        stateTest = State.validateState(state);
        assertEquals(State.WAITING, stateTest);
    }
}
