package ru.practicum.shareit.checkservice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ValidationException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

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

    @Override
    public void checkRequest(Long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new RequestNotFoundException("Запрос не найден " + requestId);
        }
    }

    @Override
    public PageRequest checkPageSize(Integer from, Integer size) {
        if (from == 0 && size == 0) {
            throw new ValidationException("\"size\" и \"from\" не может быть равно 0 ");
        }
        if (size <= 0) {
            throw new ValidationException("\"size\" должен быть больше 0 ");
        }
        if (from < 0) {
            throw new ValidationException("\"from\" должен быть больше или рано 0 ");
        }
        return PageRequest.of(from / size, size);
    }
}