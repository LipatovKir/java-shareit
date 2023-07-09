package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUsersRequests(Long userId);

    List<ItemRequestDto> getRequests(Long userId, PageRequest pageRequest);

    ItemRequestDto getRequestById(Long requestId, Long userId);

    Optional<ItemRequest> findRequestById(Long requestId);
}
