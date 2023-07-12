package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    Page<Booking> findAllByBookerOrderByStartDesc(User user, PageRequest pageRequest);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime currentDateForStart, LocalDateTime currentDateForEnd, PageRequest pageRequest);

    Page<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime currentDateForEnd, PageRequest pageRequest);

    Page<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime currentDateForStart, PageRequest pageRequest);

    Page<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item.id where i.owner.id =?1 order by b.start desc")
    Page<Booking> getBookingsByOwnerId(Long ownerId, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item.id where i.owner.id =?1 " +
            "and b.start <= current_timestamp and b.end >= current_timestamp order by b.start asc")
    Page<Booking> getCurrentBookingByOwnerId(Long ownerId, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.start >= current_timestamp " +
            "order by b.start desc")
    Page<Booking> getFutureBookingByOwnerId(Long ownerId, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.end <= current_timestamp " +
            "order by b.start desc")
    Page<Booking> getPastBookingByOwnerId(Long ownerId, PageRequest pageRequest);

    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    Page<Booking> getStateBookingByOwnerId(Long ownerId, BookingStatus status, PageRequest pageRequest);

    Booking findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime time, BookingStatus status);

    Booking findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime time, BookingStatus status);


    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.status <> 'REJECTED' " +
            "order by b.start asc")
    List<Booking> getBookingsByOwner(Long ownerId);

    @Query("select b from Booking b where b.item.id = ?1 order by b.start asc")
    List<Booking> getBookingsByItemOrderByStartAsc(Long itemId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2")
    List<Booking> getBookingsByBookerIdAndItem(Long userId, Long itemId);

}
