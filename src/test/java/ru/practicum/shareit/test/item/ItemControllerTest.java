package ru.practicum.shareit.test.item;

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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.test.constants.Constants.*;

@WebMvcTest(controllers = ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;
    ItemDto firstitemDto;
    ItemDto secondItemDto;
    CommentDto commentDto;

    @BeforeEach
    void beforeEach() {

        User user = User.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("доступно")
                .created(LocalDateTime.now())
                .authorName("Иванио")
                .build();

        firstitemDto = ItemDto.builder()
                .id(1L)
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .comments(List.of(commentDto))
                .requestId(itemRequest.getId())
                .build();

        secondItemDto = ItemDto.builder()
                .id(1L)
                .name(TEST_ITEM_SECOND)
                .description(TEST_ITEM_DESCRIPTION_SECOND)
                .available(true)
                .comments(Collections.emptyList())
                .requestId(itemRequest.getId())
                .build();
    }

    @SneakyThrows
    @Test
    void createItem() {
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(firstitemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(firstitemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstitemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstitemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(firstitemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(firstitemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(firstitemDto.getRequestId()), Long.class));
        verify(itemService, times(1)).createItem(1L, firstitemDto);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(firstitemDto);
        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(firstitemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstitemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstitemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(firstitemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(firstitemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(firstitemDto.getRequestId()), Long.class));
        verify(itemService, times(1)).updateItem(firstitemDto, 1L, 1L);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(firstitemDto);
        mvc.perform(get("/items/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstitemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstitemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(firstitemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(firstitemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(firstitemDto.getRequestId()), Long.class));
        verify(itemService, times(1)).getItemById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getAllItemsUser() {
        when(itemService.getItemsUser(anyLong(), anyInt(), anyInt())).thenReturn(List.of(firstitemDto, secondItemDto));
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstitemDto, secondItemDto))));
        verify(itemService, times(1)).getItemsUser(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getSearchItem() {
        when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(List.of(firstitemDto, secondItemDto));
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstitemDto, secondItemDto))));
        verify(itemService, times(1)).searchItem("text", 0, 10);
    }

    @SneakyThrows
    @Test
    void createComment() {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
        verify(itemService, times(1)).createComment(1L, 1L, commentDto);
    }
}