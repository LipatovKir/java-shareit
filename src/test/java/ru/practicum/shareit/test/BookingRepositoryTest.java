package ru.practicum.shareit.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    PageRequest pageRequest = PageRequest.of(0, 10);
    User userTest = new User("Test", "test@test.com");
    Item item = new Item("name", "desc", true, userTest);

    Booking booking = new Booking(LocalDateTime.now().withNano(0), LocalDateTime.now()
            .withNano(0).plusDays(1),
            item, userTest, BookingStatus.WAITING, null);

    Booking pastBooking = new Booking(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(2),
            item, userTest, BookingStatus.WAITING, null);

    Booking futureBooking = new Booking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
            item, userTest, BookingStatus.WAITING, null);

    Long userForTest;
    Long itemId;
    Long bookingId;
    Long pastBookingId;
    Long futureBookingId;

    @BeforeAll
    public void addBooking() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.save(userTest);
        userForTest = userRepository.findAll().get(0).getId();
        itemRepository.save(item);
        itemId = itemRepository.findAll().get(0).getId();
        bookingRepository.save(booking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
        bookingId = bookingRepository.findAll().get(0).getId();
        pastBookingId = bookingRepository.findAll().get(1).getId();
        futureBookingId = bookingRepository.findAll().get(2).getId();
    }

    @Test
    void findAllByBookerOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBookerOrderByStartDesc(userTest, pageRequest);
        assertEquals(3, bookings.getContent().size());
        assertEquals(futureBookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(userTest, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
        assertEquals(1, bookings.getContent().size());
        assertEquals(bookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void findAllByBookerAndEndBeforeOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(userTest, LocalDateTime.now(), pageRequest);
        assertEquals(1, bookings.getContent().size());
        assertEquals(pastBookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void findAllByBookerAndStartAfterOrderByStartDesc() {
        Page<Booking> bookings = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(userTest, LocalDateTime.now(), pageRequest);
        assertEquals(1, bookings.getContent().size());
        assertEquals(futureBookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void findAllByBookerAndStatusEquals() {
        Page<Booking> bookings = bookingRepository.findAllByBookerAndStatusEquals(userTest, BookingStatus.WAITING, pageRequest);
        assertEquals(3, bookings.getContent().size());
        assertEquals(bookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void getBookingsByOwnerId() {
        Page<Booking> bookings = bookingRepository.getBookingsByOwnerId(userForTest, pageRequest);
        assertEquals(3, bookings.getContent().size());
        assertEquals(futureBookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void getCurrentBookingByOwnerId() {
        Page<Booking> bookings = bookingRepository.getCurrentBookingByOwnerId(userForTest, pageRequest);
        assertEquals(1, bookings.getContent().size());
        assertEquals(bookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void getFutureBookingByOwnerId() {
        Page<Booking> bookings = bookingRepository.getFutureBookingByOwnerId(userForTest, pageRequest);
        assertEquals(1, bookings.getContent().size());
        assertEquals(futureBookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void getPastBookingByOwnerId() {
        Page<Booking> bookings = bookingRepository.getPastBookingByOwnerId(userForTest, pageRequest);
        assertEquals(1, bookings.getContent().size());
        assertEquals(pastBookingId, bookings.getContent().get(0).getId());
    }

    @Test
    void getStateBookingByOwnerId() {
        Page<Booking> bookings = bookingRepository.getStateBookingByOwnerId(userForTest, BookingStatus.WAITING, pageRequest);
        assertEquals(3, bookings.getContent().size());
    }

    @Test
    void getBookingsByOwner() {
        List<Booking> bookings = bookingRepository.getBookingsByOwner(userForTest);
        assertEquals(3, bookings.size());
    }

    @Test
    void getBookingsByItemOrderByStartAsc() {
        List<Booking> bookings = bookingRepository.getBookingsByItemOrderByStartAsc(itemId);
        assertEquals(3, bookings.size());
    }

    @Test
    void getBookingsByBookerIdAndItem() {
        List<Booking> bookings = bookingRepository.getBookingsByBookerIdAndItem(userForTest, itemId);
        assertEquals(3, bookings.size());
    }

    @AfterAll
    public void delete() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
