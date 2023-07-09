package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.RequestError;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addNewItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        Optional<User> requester = userRepository.findById(userId);
        if (requester.isPresent()) {
            log.info("Пользователь с id" + userId + "успешно найден");
            itemRequestDto.setCreated(LocalDateTime.now().withNano(0));
            ItemRequest itemRequest = ItemRequestMapper.toDtoItemRequest(itemRequestDto, requester.get());
            itemRequest.setRequester(requester.get());
            itemRequest = itemRequestRepository.save(itemRequest);
            log.info("Запрос с id" + itemRequestDto.getRequesterId() + "успешно сохранен");
            itemRequestDto.setId(itemRequest.getId());
            itemRequestDto.setRequesterId(itemRequest.getRequester().getId());
            return itemRequestDto;
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь с id" + userId + "не найден в базе");
        }
    }

    @Override
    public List<ItemRequestDto> getUsersRequests(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            List<ItemRequest> requests = itemRequestRepository.findRequestsByUser(userId);
            List<ItemRequestDto> requestDtos = requests.stream()
                    .map(request -> ItemRequestMapper.toItemRequestDto(request))
                    .collect(Collectors.toList());
            return requestDtos;
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь с id" + userId + " не найден");
        }
    }

    //получить список запросов, созданных другими пользователями постранично
    @Override
    public List<ItemRequestDto> getRequests(Long userId, PageRequest pageRequest) {
        if (userRepository.findById(userId).isPresent()) {
            Page<ItemRequest> pages = itemRequestRepository.findRequestsWithoutOwner(userId, pageRequest);
            List<ItemRequest> requests = pages.getContent();
            List<ItemRequestDto> requestDtos = requests.stream()
                    .map(request -> ItemRequestMapper.toItemRequestDto(request))
                    .collect(Collectors.toList());
            return requestDtos;
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь с id" + userId + " не найден");
        }
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            Optional<ItemRequest> itemRequestFromDb = itemRequestRepository.findById(requestId);
            if (itemRequestFromDb.isPresent()) {
                return ItemRequestMapper.toItemRequestDto(itemRequestFromDb.get());
            } else {
                throw new RequestError(HttpStatus.NOT_FOUND, "Запрос с id" + requestId + " не найден");
            }
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь с id" + userId + " не найден");
        }
    }

    @Override
    public Optional<ItemRequest> findRequestById(Long requestId) {
        return itemRequestRepository.findById(requestId);
    }
}
