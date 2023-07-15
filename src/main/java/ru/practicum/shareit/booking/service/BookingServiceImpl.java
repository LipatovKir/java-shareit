package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.checkservice.CheckService;

import java.time.LocalDateTime;
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
        if (itemRepository.findById(bookingDto.getItemId()).isPresent() || userRepository.findById(userId).isPresent()) {
            Item item = itemRepository.findById(bookingDto.getItemId()).get();
            checkService.checkUser(userId);
            User user = userRepository.findById(userId).get();
            Booking booking = BookingMapper.returnBooking(bookingDto);
            booking.setItem(item);
            booking.setBooker(user);
            if (item.getOwner().equals(user)) {
                throw new OwnerNotFoundException("Владелец не может бронировать свою вещь " + userId);
            }
            if (Boolean.FALSE.equals(item.getAvailable())) {
                throw new ValidationException("Вещь уже забронирована " + item.getId());
            }
            if (booking.getStart().isAfter(booking.getEnd())) {
                throw new ValidationException("Начало бронирования не может быть позже окончания.");
            }
            if (booking.getStart().isEqual(booking.getEnd())) {
                throw new ValidationException("Начало бронирования не может совпадать с окончанием.");
            }
            bookingRepository.save(booking);
            return BookingMapper.returnBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Вещь или пользователь не найдены.");
        }
    }

    @Transactional
    @Override
    public BookingResponseDto updateBooking(long userId, long bookingId, Boolean approved) {
        checkService.checkBooking(bookingId);
        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking booking = bookingRepository.findById(bookingId).get();
            if (booking.getItem().getOwner().getId() != userId) {
                throw new OwnerNotFoundException("Только владелец вещи может изменять статус " + userId);
            }
            if (Boolean.TRUE.equals(approved)) {
                if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                    throw new ValidationException("Некорректный запрос по изменению статуса.");
                }
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepository.save(booking);
            return BookingMapper.returnBookingDto(booking);
        } else {
            throw new BookingNotFoundException("Такая бронь не найдена");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponseDto getBookingById(long userId, long bookingId) {
        checkService.checkBooking(bookingId);
        if (bookingRepository.findById(bookingId).isPresent()) {
            Booking booking = bookingRepository.findById(bookingId).get();
            checkService.checkUser(userId);
            if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                return BookingMapper.returnBookingDto(booking);
            } else {
                throw new NotFoundException(User.class, "To get information about the reservation, the car of the reservation or the owner {} " + userId + "of the item can");
            }
        } else {
            throw new BookingNotFoundException("Такая бронь не найдена");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingByBooker(long userId, String state) {
        checkService.checkUser(userId);
        List<Booking> bookings;
        State bookingState = State.valueOf(state);
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
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return BookingMapper.returnBookingDtoList(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getBookingByOwner(long userId, String state) {
        checkService.checkUser(userId);
        if (itemRepository.findByOwnerId(userId).isEmpty()) {
            throw new ValidationException("У пользователя нет бронирований.");
        }
        List<Booking> bookings;
        State bookingState = State.valueOf(state);
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
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return BookingMapper.returnBookingDtoList(bookings);
    }
}

