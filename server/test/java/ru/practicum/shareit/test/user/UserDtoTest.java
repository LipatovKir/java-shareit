package ru.practicum.shareit.test.user;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.test.constants.Constants.TEST_EMAIL;
import static ru.practicum.shareit.test.constants.Constants.TEST_USER;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserDtoTest {

    @Autowired
    JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void testUserDto() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ms.Test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@test.ru");
    }
}