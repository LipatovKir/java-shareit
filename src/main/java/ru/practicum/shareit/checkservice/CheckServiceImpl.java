package ru.practicum.shareit.checkservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден " + userId);
        }
    }

    @Override
    public void checkItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Вещь не найдена " + itemId);
        }
    }

    @Override
    public void checkBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException("Бронирование не найдено " + bookingId);
        }
    }
}
