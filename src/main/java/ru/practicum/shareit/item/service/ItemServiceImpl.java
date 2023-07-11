package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.exception.ItemBadRequestException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRequestService itemRequestService;

    @Override
    public ItemDto addNewItem(UserDto userDto, ItemDto itemDto, Long requestId) {
        User user = UserMapper.makeDtoToUser(userDto);
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(user);
        if (requestId != null) {
            Optional<ItemRequest> itemRequest = itemRequestService.findRequestById(requestId);
            itemRequest.ifPresent(item::setItemRequest);
        }
        itemRepository.save(item);
        log.info("Вещь " + item.getId() + " добавлена пользователем " + user.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto putItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = ItemMapper.dtoToItem(itemDto);
        checkItemId(itemId);
        checkUserId(itemId, userId);
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            if (item.getName() != null) {
                itemOpt.get().setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemOpt.get().setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemOpt.get().setAvailable(item.getAvailable());
            }
            itemRepository.save(itemOpt.get());
            log.info("Вещь с ID:" + itemId + " обновлена пользователем: " + userId);
            return ItemMapper.toItemDto(itemOpt.get());
        }
        return itemDto;
    }

    public void checkItemsAvailability(Long itemId) {
        checkItemId(itemId);
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent() && (!item.get().getAvailable())) {
            throw new ItemBadRequestException("Вещь c id:" + item.get().getId() + " недоступна");
        }
    }

    @Override
    public ItemDtoForBooking getItemById(Long itemId, User user, List<CommentShortDto> commentsResponseDto) {
        checkItemId(itemId);
        if (itemRepository.findById(itemId).isPresent()) {
            Item item = itemRepository.findById(itemId).get();
            log.info("Вещь с ID:" + itemId + " успешно найдена");
            Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
            Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
            ItemDtoForBooking itemDtoForBooking = ItemMapper.toItemDtoForBooking(item, null, commentsResponseDto);
            if (item.getOwner().getId().equals(user.getId())) {
                if (lastBooking != null) {
                    itemDtoForBooking.setLastBooking(BookingMapper.makeBookingShortDto(lastBooking));
                }
                if (nextBooking != null) {
                    itemDtoForBooking.setNextBooking(BookingMapper.makeBookingShortDto(nextBooking));
                }
            }
            return itemDtoForBooking;
        }
        return null;
    }

    @Override
    public Item getItemByOwner(Long itemId) {
        checkItemId(itemId);
        Optional<Item> item = itemRepository.findById(itemId);
        return item.orElse(null);
    }

    @Override
    public List<ItemDtoForBooking> getItemsByUser(UserDto userDto, List<BookingShortDto> bookings, PageRequest pageRequest) {
        User user = UserMapper.makeDtoToUser(userDto);
        List<Item> items = itemRepository.findByOwnerOrderById(user, pageRequest);
        log.info("Найдены все вещи пользователя с id:" + user.getId());
        return items
                .stream()
                .map(item -> ItemMapper.toItemDtoForBooking(item, bookings, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Long userId, String text, PageRequest pageRequest) {
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        } else {
            return itemRepository
                    .search(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentShortDto createComment(Long userId, Long itemId, String text) {
        if (StringUtils.isBlank(text)) {
            throw new CommentException("Пустой комментарий.");
        }
        Item item = itemRepository.findById(itemId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (item == null) {
            throw new ItemBadRequestException("Вещь не найдена.");
        }
        if (user == null) {
            throw new BookingBadRequestException("Пользователь не найден.");
        }
        if (!checkUserIsBookerForItem(userId, itemId)) {
            throw new ItemBadRequestException("Пользователь не брал вещь в аренду.");
        }
        if (!checkCreatedCommentAfterBooking(userId, itemId)) {
            throw new BookingBadRequestException("Вещь все еще находится в аренде у пользователя.");
        }
        Comment comment = saveNewComment(itemId, text, userId);
        CommentDto commentDto = CommentMapper.commentDto(comment, user, item);
        return CommentMapper.commentDtoToResponseDto(commentDto);
    }

    @Override
    public List<CommentShortDto> getCommentList(Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        List<CommentShortDto> commentResponseList = new ArrayList<>();
        List<Comment> comments = commentRepository.getCommentsByItemId(itemId);
        List<Long> authorIds = comments.stream().map(Comment::getAuthorId)
                .collect(Collectors.toList());
        Map<Long, User> userMap = userRepository.findByUserIds(authorIds)
                .stream().collect(Collectors.toMap(User::getId, u -> u));

        for (Comment comment : comments) {
            CommentDto commentDto = CommentMapper.commentDto(comment, userMap.get(comment.getAuthorId()), item);
            commentResponseList.add(CommentMapper.commentDtoToResponseDto(commentDto));
        }
        return commentResponseList;
    }

    private boolean checkCreatedCommentAfterBooking(Long userId, Long itemId) {
        LocalDateTime createdTime = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndItem(userId, itemId);
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(createdTime)) return true;
        }
        return false;
    }

    private boolean checkUserIsBookerForItem(Long userId, Long itemId) {
        List<Booking> bookingList = bookingRepository.getBookingsByItemOrderByStartAsc(itemId);
        for (Booking booking : bookingList) {
            if (Objects.equals(booking.getBooker().getId(), userId)) return true;
        }
        return false;
    }

    private Comment saveNewComment(Long itemId, String text, Long authorId) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setText(text);
        comment.setAuthorId(authorId);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private void checkUserId(Long itemId, Long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent() && (!item.get().getOwner().getId().equals(userId))) {
            throw new OwnerNotFoundException("Редактировать вещь может только её владелец");
        }
    }

    private void checkItemId(Long itemId) {
        Optional<Item> itemFromDbe = itemRepository.findById(itemId);
        if (itemFromDbe.isEmpty()) {
            throw new OwnerNotFoundException("Вещи c id:" + itemId + " нет в списке");
        }
    }
}
