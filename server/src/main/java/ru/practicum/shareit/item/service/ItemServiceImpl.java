package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ValidationItemException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.checkservice.CheckService;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String ITEM_NOT_FOUND = "Не найдена вещь с id: ";
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CheckService checkService;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        checkService.checkUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + userId));
        Item item = ItemMapper.makeDtoInItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            checkService.checkRequest(itemDto.getRequestId());
            Optional<ItemRequest> request = itemRequestRepository.findById(itemDto.getRequestId());
            request.ifPresent(item::setRequest);
        }
        itemRepository.save(item);
        return ItemMapper.makeItemInDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        checkService.checkUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Не найден пользователь " + userId));
        checkService.checkItem(itemId);
        Item item = ItemMapper.makeDtoInItem(itemDto, user);
        item.setId(itemId);
        if (!itemRepository.findByOwnerId(userId).contains(item)) {
            throw new ItemNotFoundException("Не найдена вещь пользователя " + userId);
        }
        Item newItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new ItemNotFoundException("Не найдена вещь с идентификатором " + item.getId()));
        Optional.ofNullable(item.getName()).ifPresent(newItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(newItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(newItem::setAvailable);
        itemRepository.save(newItem);
        return ItemMapper.makeItemInDto(newItem);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemById(long itemId, long userId) {
        checkService.checkItem(itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + itemId));
        ItemDto itemDto = ItemMapper.makeItemInDto(item);
        checkService.checkUser(userId);
        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, BookingStatus.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, BookingStatus.APPROVED, LocalDateTime.now());
            itemDto.setLastBooking(lastBooking.map(BookingMapper::makeBookingShortDto).orElse(null));
            itemDto.setNextBooking(nextBooking.map(BookingMapper::makeBookingShortDto).orElse(null));
        }
        List<Comment> commentList = commentRepository.findAllByItemId(itemId);
        createCommentDtoList(commentList, itemDto);
        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsUser(long userId, Integer from, Integer size) {
        checkService.checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<ItemDto> dtoItems = new ArrayList<>();
        for (ItemDto itemDto : ItemMapper.makeItemDtoList(itemRepository.findByOwnerId(userId, pageRequest))) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), BookingStatus.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), BookingStatus.APPROVED, LocalDateTime.now());
            lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.makeBookingShortDto(booking)));
            nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.makeBookingShortDto(booking)));
            if (lastBooking.isEmpty()) {
                itemDto.setLastBooking(null);
            }
            if (nextBooking.isEmpty()) {
                itemDto.setNextBooking(null);
            }
            dtoItems.add(itemDto);
        }
        for (ItemDto itemDto : dtoItems) {
            List<Comment> commentList = commentRepository.findAllByItemId(itemDto.getId());
            createCommentDtoList(commentList, itemDto);
        }
        return dtoItems;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        PageRequest pageRequest = checkService.checkPageSize(from, size);
        if (text.equals("")) {
            return Collections.emptyList();
        } else {
            return ItemMapper.makeItemDtoList(itemRepository.search(text, pageRequest));
        }
    }

    @Transactional
    @Override
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        checkService.checkUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
        checkService.checkItem(itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND + itemId));
        LocalDateTime dateTime = LocalDateTime.now();
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED, dateTime);
        if (booking.isEmpty()) {
            throw new ValidationItemException("Пользователь " + userId + " не бронировал вещь " + itemId);
        }
        Comment comment = CommentMapper.makeDtoInComment(commentDto, item, user, dateTime);
        commentRepository.save(comment);
        return CommentMapper.makeCommentInDto(comment);
    }

    private void createCommentDtoList(List<Comment> commentList, ItemDto itemDto) {
        if (!commentList.isEmpty()) {
            itemDto.setComments(CommentMapper.makeCommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }
    }
}
