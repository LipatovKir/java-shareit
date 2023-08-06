package ru.practicum.shareit.test.item;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.test.constants.Constants.TEST_EMAIL;
import static ru.practicum.shareit.test.constants.Constants.TEST_USER;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentDtoTest {

    @Autowired
    JacksonTester<CommentDto> json;

    @SneakyThrows
    @Test
    void testCommentDto() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("acceptable")
                .created(LocalDateTime.now())
                .authorName(userDto.getName())
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("acceptable");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Ms.Test");
    }
}