package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.checkservice.CheckService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CheckService checkService;

    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto, long userId) {
        checkService.checkItem(bookingDto.getItemId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена: " + bookingDto.getItemId()));
        checkService.checkUser(userId);
        Booking booking = BookingMapper.makeDtoInBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        if (item.getOwner().equals(user)) {
            throw new OwnerNotFoundException("Владелец не может бронировать свою вещь: " + userId);
        }
        if (BooleanUtils.isFalse(item.getAvailable())) {
            throw new ValidationException("Вещь уже забронирована: " + item.getId());
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Начало бронирования не может быть позже окончания: " + booking.getStart() + ", " + booking.getEnd());
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Начало бронирования не может совпадать с окончанием: " + booking.getStart() + ", " + booking.getEnd());
        }
        bookingRepository.save(booking);
        return BookingMapper.makeBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingResponseDto updateBooking(long userId, long bookingId, Boolean approved) {
        checkService.checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена: " + bookingId));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new OwnerNotFoundException("Только владелец вещи может изменять статус: " + userId);
        }
        if (Boolean.TRUE.equals(approved)) {
            if (booking.getStatus() == BookingStatus.APPROVED) {
                throw new ValidationException("Некорректный запрос по изменению статуса." + true);
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.makeBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponseDto getBookingById(long userId, long bookingId) {
        checkService.checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена: " + bookingId));
        checkService.checkUser(userId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.makeBookingDto(booking);
        } else {
            throw new UserNotFoundException("Не удалось получить информацию по бронированию: " + userId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingByBooker(long userId, String state) {
        checkService.checkUser(userId);
        List<Booking> bookings;
        State bookingState = State.validateState(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        return BookingMapper.makeBookingDtoList(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingByOwner(long userId, String state) {
        checkService.checkUser(userId);
        if (itemRepository.findByOwnerId(userId).isEmpty()) {
            throw new ValidationException("У пользователя нет бронирований." + userId);
        }
        List<Booking> bookings;
        State bookingState = State.validateState(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        return BookingMapper.makeBookingDtoList(bookings);
    }
}

