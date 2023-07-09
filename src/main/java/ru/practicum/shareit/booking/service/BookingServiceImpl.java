package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.enums.StatusDto;
import ru.practicum.shareit.exceptions.RequestError;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public BookingDto addNewBooking(UserDto userDto, Item item, BookingDto bookingDto) {
        checkBookingDate(bookingDto.getStart(), bookingDto.getEnd());
        User user = UserMapper.dtoToUser(userDto);
        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь не может арендовать собственную вещь");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        // После создания запрос находится в статусе WAITING — «ожидает подтверждения».
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        log.info("Пользователь: " + userDto.getId() + " создал бронирование");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto patch(Long bookingId, Long userId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (!booking.get().getStatus().equals(Status.WAITING)) {
            throw new RequestError(HttpStatus.BAD_REQUEST, "бронь уже обработана");
        }
        if (booking.isPresent()) {
            if (booking.get().getItem().getOwner().getId().equals(userId)) {
                if (approved) {
                    booking.get().setStatus(Status.APPROVED);
                } else {
                    booking.get().setStatus(Status.REJECTED);
                }
                bookingRepository.save(booking.get());
            } else {
                throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь не является владельцем вещи");
            }
            return BookingMapper.toBookingDto(booking.get());
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Такой вещи нет");
        }
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            //Может быть выполнено либо автором бронирования, либо владельцем вещи,
            if ((booking.get().getItem().getOwner().getId().equals(userId)) || (
                    booking.get().getBooker().getId().equals(userId))) {
                return BookingMapper.toBookingDto(booking.get());
            } else {
                throw new RequestError(HttpStatus.NOT_FOUND, "Нет доступа на просмотр");
            }
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Такая бронь не найдена");
        }
    }

    //Получение списка всех бронирований текущего пользователя.
    //Эндпоинт — GET /bookings?state={state}. Параметр state необязательный и по умолчанию
    //равен ALL (англ. «все»). Также он может принимать значения CURRENT (англ. «текущие»),
    //**PAST** (англ. «завершённые»), FUTURE (англ. «будущие»), WAITING (англ. «ожидающие подтверждения»),
    //REJECTED (англ. «отклонённые»).
    //Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    @Override
    public List<BookingDto> getBookingsByUserAndState(User user, StatusDto statusDto, PageRequest pageRequest) {
        List<Booking> bookings = new ArrayList<>();
        Page<Booking> bookingPage;
        LocalDateTime currentDate = LocalDateTime.now();
        switch (statusDto) {
            case ALL:
                bookingPage = bookingRepository.findAllByBookerOrderByStartDesc(user, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case CURRENT:
                bookingPage = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, currentDate, currentDate, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case PAST:
                bookingPage = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(user, currentDate, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case FUTURE:
                bookingPage = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(user, currentDate, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                Status status = Status.valueOf(statusDto.toString());
                bookingPage = bookingRepository.findAllByBookerAndStatusEquals(user, status, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            default:
                throw new RequestError(HttpStatus.BAD_REQUEST, "Некорректный параметр state");
        }
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    //Эндпоинт — GET /bookings/owner?state={state}.
    //Этот запрос имеет смысл для владельца хотя бы одной вещи.
    //Работа параметра state аналогична его работе в предыдущем сценарии.
    @Override
    public List<BookingDto> getBookingsByOwnerAndState(User user, StatusDto statusDto, PageRequest pageRequest) {
        List<Booking> bookings = new ArrayList<>();
        Page<Booking> bookingPage;
        switch (statusDto) {
            case ALL:
                bookingPage = bookingRepository.getBookingsByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case CURRENT:
                bookingPage = bookingRepository.getCurrentBookingByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case PAST:
                bookingPage = bookingRepository.getPastBookingByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case FUTURE:
                bookingPage = bookingRepository.getFutureBookingByOwnerId(user.getId(), pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                Status status = Status.valueOf(statusDto.toString());
                bookingPage = bookingRepository.getStateBookingByOwnerId(user.getId(), status, pageRequest);
                bookings = bookingPage.getContent();
                return bookings.stream().map(booking -> BookingMapper.toBookingDto(booking)).collect(Collectors.toList());
            default:
                throw new RequestError(HttpStatus.BAD_REQUEST, "некорректный state");
        }
    }

    @Override
    public List<BookingSmallDto> getBookingsByItem(Long itemId) {
        return bookingRepository.getBookingsByItem(itemId, LocalDateTime.now().minusSeconds(5))
                .stream().map(BookingMapper::toBookingSmallDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingSmallDto> getBookingsByOwner(UserDto userDto) {
        return bookingRepository.getBookingsByOwner(userDto.getId())
                .stream().map(BookingMapper::toBookingSmallDto)
                .collect(Collectors.toList());
    }

    private void checkBookingDate(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(LocalDateTime.now())) {
            throw new RequestError(HttpStatus.BAD_REQUEST, "Дата окончания меньше текущей");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new RequestError(HttpStatus.BAD_REQUEST, "Дата старта меньше текущей");
        }
        if (start.isAfter(end)) {
            throw new RequestError(HttpStatus.BAD_REQUEST, "Дата старта позже даты окончания");
        }
        if (Objects.equals(start, end)) {
            throw new RequestError(HttpStatus.BAD_REQUEST, "Дата старта не может быть равной дате окончания");
        }
    }
}
