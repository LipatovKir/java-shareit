package ru.practicum.shareit.test.booking_test;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.ValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.checkservice.CheckService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.test.constants.Constants.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    @MockBean
    CheckService checkService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    BookingRepository bookingRepository;

    User firstUser;
    User secondUser;
    Item item;
    ItemDto itemDto;
    Booking firstBooking;
    Booking secondBooking;
    BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        secondUser = User.builder()
                .id(2L)
                .name(TEST_USER_SECOND)
                .email(TEST_EMAIL_SECOND)
                .build();

        item = Item.builder()
                .id(1L)
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .owner(firstUser)
                .build();
        itemDto = ItemMapper.makeItemInDto(item);

        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(firstUser)
                .status(BookingStatus.APPROVED)
                .build();

        secondBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(firstUser)
                .status(BookingStatus.APPROVED)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 7, 5, 0, 0))
                .end(LocalDateTime.of(2023, 10, 12, 0, 0))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBooking() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);
        BookingResponseDto bookingOutDtoTest = bookingService.createBooking(bookingDto, anyLong());
        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.makeUserInDto(secondUser));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingBadOwner() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        assertThrows(OwnerNotFoundException.class, () -> bookingService.createBooking(bookingDto, 1234L));
    }

    @Test
    void createBookingItemBooked() {
        item.setAvailable(false);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, 1234L));
    }

    @Test
    void createBookingBadEndTime() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        bookingDto.setEnd(LocalDateTime.of(2022, 10, 12, 0, 0));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, 1234L));
    }

    @Test
    void createBookingBadStartTime() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(secondUser));
        bookingDto.setStart(LocalDateTime.of(2023, 10, 12, 0, 0));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, 1234L));
    }

    @Test
    void updateBookingBadUser() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(secondBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(secondBooking);
        assertThrows(ValidationException.class, () -> bookingService.updateBooking(1, 1213L, true));
    }

    @Test
    void updateBookingBadStatus() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(firstBooking);
        assertThrows(ValidationException.class, () -> bookingService.updateBooking(1, 1213L, true));
    }

    @Test
    void getBookingById() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(firstBooking));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        BookingResponseDto bookingOutDtoTest = bookingService.getBookingById(firstUser.getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.makeUserInDto(firstUser));
    }

    @Test
    void getBookingsByBooker() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        String state = "ALL";
        List<BookingResponseDto> bookingOutDtoTest = bookingService.getBookingByBooker(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "CURRENT";
        bookingOutDtoTest = bookingService.getBookingByBooker(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "PAST";
        bookingOutDtoTest = bookingService.getBookingByBooker(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "FUTURE";
        bookingOutDtoTest = bookingService.getBookingByBooker(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "WAITING";
        bookingOutDtoTest = bookingService.getBookingByBooker(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "REJECTED";
        bookingOutDtoTest = bookingService.getBookingByBooker(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
    }

    @Test
    void getBookingsByOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        String state = "ALL";
        List<BookingResponseDto> bookingOutDtoTest = bookingService.getBookingByOwner(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "CURRENT";
        bookingOutDtoTest = bookingService.getBookingByOwner(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "PAST";
        bookingOutDtoTest = bookingService.getBookingByOwner(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "FUTURE";
        bookingOutDtoTest = bookingService.getBookingByOwner(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "WAITING";
        bookingOutDtoTest = bookingService.getBookingByOwner(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(firstBooking)));
        state = "REJECTED";
        bookingOutDtoTest = bookingService.getBookingByOwner(firstUser.getId(), state, 5, 10);
        assertEquals(bookingOutDtoTest.get(0).getId(), firstBooking.getId());
        assertEquals(bookingOutDtoTest.get(0).getStatus(), firstBooking.getStatus());
        assertEquals(bookingOutDtoTest.get(0).getBooker(), UserMapper.makeUserInDto(firstUser));
    }

    @Test
    void getBookingsForNotHaveItemsByOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of());
        assertThrows(ValidationException.class, () -> bookingService.getBookingByOwner(1, "APPROVED", 5, 10));
    }
}
