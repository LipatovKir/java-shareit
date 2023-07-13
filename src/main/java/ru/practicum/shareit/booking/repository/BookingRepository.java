package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    List<Booking> findAllByBookerOrderByStartDesc(User user);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime currentDateForStart, LocalDateTime currentDateForEnd);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime currentDateForEnd);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime currentDateForStart);

    List<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status);

    Booking findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime time, BookingStatus status);

    Booking findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime time, BookingStatus status);


    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item.id where i.owner.id =?1 order by b.start desc")
    List<Booking> getBookingsByOwnerId(Long ownerId);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item.id where i.owner.id =?1 " +
            "and b.start <= current_timestamp and b.end >= current_timestamp order by b.start desc")
    List<Booking> getCurrentBookingByOwnerId(Long ownerId);


    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.start >= current_timestamp " +
            "order by b.start desc")
    List<Booking> getFutureBookingByOwnerId(Long ownerId);

    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.end <= current_timestamp " +
            "order by b.start desc")
    List<Booking> getPastBookingByOwnerId(Long ownerId);

    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> getStateBookingByOwnerId(Long ownerId, BookingStatus status);

    @Query("select b from Booking b where b.item.id = ?1 " +
            "order by b.start asc")
    List<Booking> getBookingsByItem(Long itemId);

    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.status <> 'REJECTED' " +
            "order by b.start asc")
    List<Booking> getBookingsByOwner(Long ownerId);

    @Query("select b from Booking b where b.item.id = ?1 order by b.start asc")
    List<Booking> getBookingsByItemOrderByStartAsc(Long itemId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2")
    List<Booking> getBookingsByBookerIdAndItem(Long userId, Long itemId);
}

