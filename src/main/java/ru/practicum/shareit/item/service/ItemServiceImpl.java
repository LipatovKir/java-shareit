package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingSmallDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exceptions.RequestError;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
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
        User user = UserMapper.dtoToUser(userDto);
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(user);
        if (requestId != null) {
            Optional<ItemRequest> itemRequest = itemRequestService.findRequestById(requestId);
            if (itemRequest.isPresent()) {
                item.setItemRequest(itemRequest.get());
            }
        }
        itemRepository.save(item);
        log.info("Вещь с ID:" + item.getId() + " добавлена пользователем с id:" + user.getId());
        return ItemMapper.toItemDto(item);
    }

    //Редактирование вещи.
    // Изменить можно название, описание и статус доступа к аренде.
    // Редактировать вещь может только её владелец.
    @Override
    public ItemDto putItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = ItemMapper.dtoToItem(itemDto);
        checkItemId(itemId);
        checkUserId(itemId, userId);
        Optional<Item> itemFromDbe = itemRepository.findById(itemId);
        if (item.getName() != null) {
            itemFromDbe.get().setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemFromDbe.get().setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromDbe.get().setAvailable(item.getAvailable());
        }
        itemRepository.save(itemFromDbe.get());
        log.info("Вещь с ID:" + itemId + " обновлена пользователем с id:" + userId);
        return ItemMapper.toItemDto(itemFromDbe.get());
    }

    public void checkItemsAvailability(Long itemId) {
        checkItemId(itemId);
        Optional<Item> itemFromDbe = itemRepository.findById(itemId);
        if (!itemFromDbe.get().getAvailable()) {
            throw new RequestError(HttpStatus.BAD_REQUEST, "Вещь c id:" + itemFromDbe.get().getId() + " недоступна");
        }
    }

    //Просмотр информации о конкретной вещи по её идентификатору.
    //Информацию о вещи может просмотреть любой пользователь.
    @Override
    public ItemDtoForBooking getItemById(Long itemId, User user, List<CommentResponseDto> commentsResponseDto) {
        checkItemId(itemId);
        Item item = itemRepository.findById(itemId).get();
        log.info("Вещь с ID:" + itemId + " успешно найдена");
        Booking lastBooking = bookingRepository.findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(itemId, LocalDateTime.now(), Status.APPROVED);
        Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), Status.APPROVED);
        ItemDtoForBooking itemDtoForBooking = ItemMapper.toItemDtoForBooking(item, null, commentsResponseDto);
        if (item.getOwner().getId().equals(user.getId())) {
            if (lastBooking != null) {
                itemDtoForBooking.setLastBooking(BookingMapper.toBookingSmallDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDtoForBooking.setNextBooking(BookingMapper.toBookingSmallDto(nextBooking));
            }
        }
        return itemDtoForBooking;
    }

    @Override
    public Item getItemByOwner(Long itemId) {
        checkItemId(itemId);
        Item item = itemRepository.findById(itemId).get();
        return item;
    }

    //Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой.
    @Override
    public List<ItemDtoForBooking> getItemsByUser(UserDto userDto, List<BookingSmallDto> bookings, PageRequest pageRequest) {
        User user = UserMapper.dtoToUser(userDto);
        List<Item> itemsForOwner = new ArrayList<>();
        itemsForOwner = itemRepository.findByOwnerOrderById(user, pageRequest);
        log.info("Найдены все вещи пользователя с id:" + user.getId());
        List<CommentResponseDto> commentsResponseDto = null;
        return itemsForOwner
                .stream()
                .map(item -> ItemMapper.toItemDtoForBooking(item, bookings, commentsResponseDto))
                .collect(Collectors.toList());
    }

    //Поиск вещи потенциальным арендатором.
    //Пользователь передаёт в строке запроса текст, и система ищет вещи,
    //содержащие этот текст в названии или описании.
    //Происходит по эндпойнту /items/search?text={text}, в text передаётся текст для поиска.
    //Проверьте, что поиск возвращает только доступные для аренды вещи.
    @Override
    public List<ItemDto> search(Long userId, String text, PageRequest pageRequest) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository
                    .search(text)
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentResponseDto createComment(Long userId, Long itemId, String text) {
        if (text.isEmpty()) {
            log.warn("Пользователь {} пытался оставить пустой комментарий {}", userId, itemId);
            throw new RequestError(HttpStatus.BAD_REQUEST, "Пустой комментарий");
        }
        Item item = itemRepository.findById(itemId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (item == null) {
            log.warn("Пользователь {} пытался оставить комментарий ненайденной вещи {}", user, itemId);
            throw new RequestError(HttpStatus.BAD_REQUEST, "Вещь не найдена");
        }
        if (user == null) {
            log.warn("Ошибка при добавлении комментария, пользователь {} не найден", userId);
            throw new RequestError(HttpStatus.BAD_REQUEST, "Пользователь не найден");
        }
        if (!checkUserIsBookerForItem(userId, itemId)) {
            log.warn("Ошибка при добавлении комментария, пользователь {} не брал в аренду вещь {}", user, item);
            throw new RequestError(HttpStatus.BAD_REQUEST, "Пользователь не брал вещь в аренду");
        }
        if (!checkCreatedCommentAfterBooking(userId, itemId)) {
            log.warn("Ошибка при добавлении комментария, пользователь {} не закончил аренду {}", user, item);
            throw new RequestError(HttpStatus.BAD_REQUEST, "Вещь все еще находится в аренде у пользователя");
        }
        Comment comment = saveNewComment(itemId, text, userId);
        CommentDto commentDto = CommentMapper.commentDto(comment, user, item);
        return CommentMapper.commentDtoToResponseDto(commentDto);
    }

    @Override
    public List<CommentResponseDto> getCommentList(Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        List<CommentResponseDto> commentResponseList = new ArrayList<>();
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

    private Boolean checkUserId(Long itemId, Long userId) {
        Optional<Item> itemFromDbe = itemRepository.findById(itemId);
        if (itemFromDbe.get().getOwner().getId().equals(userId)) {
            return true;
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Редактировать вещь может только её владелец");
        }
    }

    private Boolean checkItemId(Long itemId) {
        Optional<Item> itemFromDbe = itemRepository.findById(itemId);
        if (itemFromDbe.isPresent()) {
            return true;
        } else {
            throw new RequestError(HttpStatus.NOT_FOUND, "Вещи c id:" + itemId + " нет в списке");
        }
    }
}
