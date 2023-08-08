package ru.practicum.shareit.test.item;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.test.constants.Constants.*;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemDtoTest {

    @Autowired
    JacksonTester<ItemDto> json;

    @SneakyThrows
    @Test
    void testItemDto() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .requestId(userDto.getId())
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("коврик");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("отличный коврик для пола");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}