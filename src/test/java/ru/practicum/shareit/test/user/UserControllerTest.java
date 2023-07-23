package ru.practicum.shareit.test.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.test.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private UserDto firstUser;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        firstUser = UserDto.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();
    }

    @SneakyThrows
    @Test
    void createUser() {
        when(userService.createUser(any(UserDto.class))).thenReturn(firstUser);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName()), String.class))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail()), String.class));
        verify(userService, times(1)).createUser(firstUser);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        when(userService.updateUser(any(UserDto.class), anyLong())).thenReturn(firstUser);
        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(firstUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName()), String.class))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail()), String.class));
        verify(userService, times(1)).updateUser(firstUser, 1L);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).deleteUser(1L);
    }

    @SneakyThrows
    @Test
    void getUser() {
        when(userService.getUserById(1L)).thenReturn(firstUser);
        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstUser.getName()), String.class))
                .andExpect(jsonPath("$.email", is(firstUser.getEmail()), String.class));
        verify(userService, times(1)).getUserById(1L);
    }
}