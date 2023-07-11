package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.exception.BookingException;
import ru.practicum.shareit.booking.exception.BookingStateException;
import ru.practicum.shareit.booking.exception.BookingTimeException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto addBooking(UserDto userDto, Item item, BookingDto bookingDto) {
        checkBookingDate(bookingDto.getStart(), bookingDto.getEnd());
        User user = UserMapper.makeDtoToUser(userDto);
        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new BookingException("Пользователь не может арендовать собственную вещь.");
        }
        Booking booking = BookingMapper.makeBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        log.info("Пользователь: " + userDto.getId() + " создал бронирование.");
        return BookingMapper.makeBookingDto(booking);
    }

    @Override
    public BookingDto patch(Long bookingId, Long userId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            if (!booking.get().getStatus().equals(BookingStatus.WAITING)) {
                throw new BookingBadRequestException("Запрос уже обработан.");
            }
            if (booking.get().getItem().getOwner().getId().equals(userId)) {
                if (Boolean.TRUE.equals(approved)) {
                    booking.get().setStatus(BookingStatus.APPROVED);
                } else {
                    booking.get().setStatus(BookingStatus.REJECTED);
                }
                bookingRepository.save(booking.get());
            } else {
                throw new BookingException("Пользователь не является владельцем вещи.");
            }
            return BookingMapper.makeBookingDto(booking.get());
        } else {
            throw new BookingException("Искомая вещь не найдена.");
        }
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            if ((booking.get().getItem().getOwner().getId().equals(userId)) || (
                    booking.get().getBooker().getId().equals(userId))) {
                return BookingMapper.makeBookingDto(booking.get());
            } else {
                throw new BookingException("Нет доступа на просмотр.");
            }
        } else {
            throw new BookingException("Такая бронь не найдена.");
        }
    }

    @Override
    public List<BookingDto> getBookingsByUserAndState(User user, State statusDto, PageRequest pageRequest) {
        List<Booking> bookings;
        Page<Booking> bookingPage;
        LocalDateTime currentDate = LocalDateTime.now();
        switch (statusDto) {
            case ALL:
                bookingPage = bookingRepository.findAllByBookerOrderByStartDesc(user, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case CURRENT:
                bookingPage = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, currentDate, currentDate, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case PAST:
                bookingPage = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(user, currentDate, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case FUTURE:
                bookingPage = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(user, currentDate, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(statusDto.toString());
                bookingPage = bookingRepository.findAllByBookerAndStatusEquals(user, status, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            default:
                throw new BookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwnerAndState(User user, State state, PageRequest pageRequest) {
        List<Booking> bookings;
        Page<Booking> bookingPage;
        switch (state) {
            case ALL:
                bookingPage = bookingRepository.getBookingsByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case CURRENT:
                bookingPage = bookingRepository.getCurrentBookingByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case PAST:
                bookingPage = bookingRepository.getPastBookingByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case FUTURE:
                bookingPage = bookingRepository.getFutureBookingByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                BookingStatus status = BookingStatus.valueOf(state.toString());
                bookingPage = bookingRepository.getStateBookingByOwnerId(user.getId(), status, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(BookingMapper::makeBookingDto).collect(Collectors.toList());
            default:
                throw new BookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingShortDto> getBookingsByOwner(UserDto userDto) {
        return bookingRepository.getBookingsByOwner(userDto.getId())
                .stream().map(BookingMapper::makeBookingShortDto)
                .collect(Collectors.toList());
    }

    private void checkBookingDate(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(LocalDateTime.now())) {
            throw new BookingTimeException("Дата окончания бронирования раньше текущей");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new BookingTimeException("Дата начала бронирования раньше текущей");
        }
        if (start.isAfter(end)) {
            throw new BookingTimeException("Дата начала бронирования позже даты окончания");
        }
        if (Objects.equals(start, end)) {
            throw new BookingTimeException("Дата начала бронирования не может быть равной дате окончания");
        }
    }
}
