package ru.practicum.shareit.test.check_state_test;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ValidationException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.checkservice.CheckService;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;



@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CheckServiceTest {

    @Autowired
    CheckService checkService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @Test
    void checkUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> checkService.checkUser(1L));
    }

    @Test
    void checkItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ItemNotFoundException.class, () -> checkService.checkItem(1L));
    }

    @Test
    void checkBooking() {
        when(bookingRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(BookingNotFoundException.class, () -> checkService.checkBooking(1L));
    }

    @Test
    void checkRequest() {
        when(itemRequestRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(RequestNotFoundException.class, () -> checkService.checkRequest(1L));
    }

    @Test
    void checkPageSize() {
        assertThrows(ValidationException.class, () -> checkService.checkPageSize(0, 0));
        assertThrows(ValidationException.class, () -> checkService.checkPageSize(5, -5));
        assertThrows(ValidationException.class, () -> checkService.checkPageSize(5, 0));
        assertThrows(ValidationException.class, () -> checkService.checkPageSize(-5, 5));
    }
}