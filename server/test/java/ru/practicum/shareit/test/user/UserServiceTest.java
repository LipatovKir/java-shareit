package ru.practicum.shareit.test.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.test.constants.Constants.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    User firstUser;
    User secondUser;
    UserDto firstUserDto;
    UserDto secondUserDto;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();
        firstUserDto = UserMapper.makeUserInDto(firstUser);

        secondUser = User.builder()
                .id(2L)
                .name(TEST_USER_SECOND)
                .email(TEST_EMAIL_SECOND)
                .build();
        secondUserDto = UserMapper.makeUserInDto(secondUser);
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(firstUser);
        UserDto userDtoTest = userService.createUser(firstUserDto);
        assertEquals(userDtoTest.getId(), firstUserDto.getId());
        assertEquals(userDtoTest.getName(), firstUserDto.getName());
        assertEquals(userDtoTest.getEmail(), firstUserDto.getEmail());
        verify(userRepository, times(1)).save(firstUser);
    }

    @Test
    void updateUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(firstUser));
        when(userRepository.save(any(User.class))).thenReturn(firstUser);
        firstUserDto.setName("Uncle test");
        firstUserDto.setEmail("Uncle@test.ru");
        UserDto userDtoUpdated = userService.updateUser(firstUserDto, 1L);
        assertEquals(userDtoUpdated.getName(), firstUserDto.getName());
        assertEquals(userDtoUpdated.getEmail(), firstUserDto.getEmail());
        verify(userRepository, times(1)).findByEmail(firstUser.getEmail());
        verify(userRepository, times(1)).save(firstUser);
    }

    @Test
    void updateUserWrongEmail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(firstUser));
        firstUserDto.setEmail("");
        assertThrows(EmailException.class, () -> userService.updateUser(firstUserDto, 2L));
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void getUserById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        UserDto userDtoTest = userService.getUserById(1L);
        assertEquals(userDtoTest.getId(), firstUserDto.getId());
        assertEquals(userDtoTest.getName(), firstUserDto.getName());
        assertEquals(userDtoTest.getEmail(), firstUserDto.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(firstUser, secondUser));
        List<UserDto> userDtoList = userService.getAllUsers();
        assertEquals(userDtoList, List.of(firstUserDto, secondUserDto));
        verify(userRepository, times(1)).findAll();
    }
}