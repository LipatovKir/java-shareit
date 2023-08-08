package ru.practicum.shareit.test.request;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.test.constants.Constants.*;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class RequestDtoTest {

    @Autowired
    JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void testItemRequestDto() {
        LocalDateTime created = LocalDateTime.of(2023, 8, 4, 0, 0);
        ItemDto itemDto = ItemDto.builder()
                .requestId(1L)
                .name(TEST_ITEM_SECOND)
                .description(TEST_ITEM_DESCRIPTION_SECOND)
                .available(true)
                .comments(Collections.emptyList())
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(TEST_REQUEST_DESCRIPTION)
                .created(created)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Запрос аренды вещи");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }
}