package ru.practicum.shareit.test.booking_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.test.constants.Constants.*;


@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    BookingDto bookingDto;
    BookingResponseDto bookingFirst;
    BookingResponseDto bookingSecond;

    @BeforeEach
    void beforeEach() {

        UserDto user = UserDto.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .requestId(1L)
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 10, 9, 0, 0))
                .end(LocalDateTime.of(2023, 10, 9, 12, 0))
                .build();

        bookingFirst = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 10, 9, 13, 0))
                .end(LocalDateTime.of(2023, 10, 9, 18, 0))
                .item(itemDto)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        bookingSecond = BookingResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 10, 9, 19, 0))
                .end(LocalDateTime.of(2023, 10, 9, 20, 0))
                .item(itemDto)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @SneakyThrows
    @Test
    void createBooking() {
        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenReturn(bookingFirst);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingFirst.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingFirst.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingFirst.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingFirst.getItem().getId()), Long.class));
        verify(bookingService, times(1)).createBooking(bookingDto, 1L);
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingFirst);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingFirst.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingFirst.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingFirst.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingFirst.getItem().getId()), Long.class));
        verify(bookingService, times(1)).updateBooking(1L, 1L, true);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingFirst);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingFirst.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingFirst.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingFirst.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingFirst.getItem().getId()), Long.class));
        verify(bookingService, times(1)).getBookingById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByBookerId() {
        when(bookingService.getBookingByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingFirst, bookingSecond));
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingFirst, bookingSecond))));
        verify(bookingService, times(1)).getBookingByBooker(1L, "ALL", 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllBookingsForAllItemsByOwnerId() {
        when(bookingService.getBookingByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingFirst, bookingSecond));
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingFirst, bookingSecond))));
        verify(bookingService, times(1)).getBookingByOwner(1L, "ALL", 0, 10);
    }
}


