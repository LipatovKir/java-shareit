package ru.practicum.shareit.test.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.test.constants.Constants.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class RequestServiceTest {

    @Autowired
    ItemRequestService itemRequestService;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    UserRepository userRepository;

    User firstUser;
    ItemRequest firstItemRequest;
    ItemRequestDto itemRequestDto;
    Item item;

    @BeforeEach
    void beforeEach() {
        firstUser = User.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        firstItemRequest = ItemRequest.builder()
                .id(1L)
                .description(TEST_REQUEST_DESCRIPTION)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .owner(firstUser)
                .request(firstItemRequest)
                .build();
        itemRequestDto = ItemRequestDto.builder().description("Запрос аренды вещи").build();
    }

    @Test
    void createRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(firstItemRequest);
        ItemRequestDto itemRequestDtoTest = itemRequestService.createRequest(itemRequestDto, firstUser.getId());
        assertEquals(itemRequestDtoTest.getId(), firstItemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), firstItemRequest.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedAsc(anyLong())).thenReturn(List.of(firstItemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequests(firstUser.getId()).get(0);
        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());
        verify(itemRequestRepository, times(1)).findByRequesterIdOrderByCreatedAsc(anyLong());
    }

    @Test
    void getRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstItemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequestById(firstUser.getId(), firstItemRequest.getId());
        assertEquals(itemRequestDtoTest.getId(), firstItemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), firstItemRequest.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), firstUser.getId());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void addItemsToRequest() {
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestDto itemRequestDtoTest = itemRequestService.addItemsToRequest(firstItemRequest);
        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), firstUser.getId());
        verify(itemRepository, times(1)).findByRequestId(anyLong());
    }
}