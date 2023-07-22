package ru.practicum.shareit.test.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.checkservice.CheckService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.test.constants.Constants.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    BookingRepository bookingRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ItemRequestRepository itemRequestRepository;

    @MockBean
    CheckService checkService;

    User user;
    Item item;
    ItemDto itemDto;
    Comment comment;
    CommentDto commentDto;
    ItemRequest itemRequest;
    Booking firstBooking;
    Booking secondBooking;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description(TEST_REQUEST_DESCRIPTION_SECOND)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name(TEST_ITEM)
                .description(TEST_ITEM_DESCRIPTION)
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.makeItemInDto(item);

        comment = Comment.builder()
                .id(1L)
                .author(user)
                .created(LocalDateTime.now())
                .text("Отлично, всем советую.")
                .build();

        commentDto = CommentMapper.makeCommentInDto(comment);

        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();

        secondBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDtoTest = itemService.createItem(user.getId(), itemDto);
        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto itemDtoTest = itemService.updateItem(itemDto, item.getId(), user.getId());
        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void getItemById() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), BookingStatus.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), BookingStatus.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(secondBooking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        ItemDto itemDtoTest = itemService.getItemById(item.getId(), user.getId());
        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemsUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), BookingStatus.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), BookingStatus.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(secondBooking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
    }

    @Test
    void searchItem() {
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(itemRepository.search(anyString(), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(item)));
        ItemDto itemDtoTest = itemService.searchItem("text", 5, 10).get(0);
        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());
        verify(itemRepository, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void searchItemEmptyText() {
        when(checkService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        List<ItemDto> itemDtoTest = itemService.searchItem("", 5, 10);
        assertTrue(itemDtoTest.isEmpty());
        verify(itemRepository, times(0)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void createComment() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(Optional.of(firstBooking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto commentDtoTest = itemService.createComment(user.getId(), item.getId(), commentDto);
        assertEquals(commentDtoTest.getText(), comment.getText());
        assertEquals(commentDtoTest.getAuthorName(), comment.getAuthor().getName());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createCommentUserNotBookingItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(Optional.empty());
        assertThat(commentDto.getText(), is(Matchers.notNullValue()));
    }
}
