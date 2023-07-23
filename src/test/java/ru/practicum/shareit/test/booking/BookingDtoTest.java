package ru.practicum.shareit.test.booking;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.test.constants.Constants.*;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingDtoTest {

    @Autowired
    JacksonTester<BookingResponseDto> json;

    @SneakyThrows
    @Test
    void testBookingDto() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 9, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 9, 10, 0);
        UserDto user = UserDto.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .build();

        BookingResponseDto bookingOutDto = BookingResponseDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(user)
                .item(itemDto)
                .build();

        JsonContent<BookingResponseDto> result = json.write(bookingOutDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Ms.Test");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("test@test.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("коврик");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }
}