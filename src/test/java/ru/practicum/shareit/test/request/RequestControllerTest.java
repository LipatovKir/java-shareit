package ru.practicum.shareit.test.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.test.constants.Constants.*;

@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class RequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    ItemRequestDto firstRequestDto;
    ItemRequestDto secondRequestDto;

    @BeforeEach
    void beforeEach() {

        firstRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description(TEST_REQUEST_DESCRIPTION)
                .created(LocalDateTime.now())
                .build();

        secondRequestDto = ItemRequestDto.builder()
                .id(2L)
                .description(TEST_ITEM_DESCRIPTION_SECOND)
                .created(LocalDateTime.now())
                .build();
    }

    @SneakyThrows
    @Test
    void createRequest() {
        when(itemRequestService.createRequest(any(ItemRequestDto.class), anyLong())).thenReturn(firstRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(firstRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(firstRequestDto.getDescription()), String.class));
        verify(itemRequestService, times(1)).createRequest(firstRequestDto, 1L);
    }

    @SneakyThrows
    @Test
    void getRequests() {
        when(itemRequestService.getRequests(anyLong())).thenReturn(List.of(firstRequestDto, secondRequestDto));
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstRequestDto, secondRequestDto))));
        verify(itemRequestService, times(1)).getRequests(1L);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(firstRequestDto, secondRequestDto));
        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstRequestDto, secondRequestDto))));
        verify(itemRequestService, times(1)).getAllRequests(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(firstRequestDto);
        mvc.perform(get("/requests/{requestId}", firstRequestDto.getId())
                        .content(mapper.writeValueAsString(firstRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(firstRequestDto.getDescription()), String.class));
        verify(itemRequestService, times(1)).getRequestById(1L, 1L);
    }
}
