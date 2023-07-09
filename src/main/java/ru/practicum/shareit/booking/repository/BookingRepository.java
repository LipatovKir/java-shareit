package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    //Получение списка всех бронирований текущего пользователя.
    Page<Booking> findAllByBookerOrderByStartDesc(User user, PageRequest pageRequest);

    //CURRENT - текущая дата между датой начала и окнчания заявки
    //Бронирования должны возвращаться отсортированными по дате от более новых к более старым.
    Page<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime currentDateForStart, LocalDateTime currentDateForEnd, PageRequest pageRequest);

    //PAST (англ. «завершённые»),
    Page<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime currentDateForEnd, PageRequest pageRequest);

    Page<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime currentDateForStart, PageRequest pageRequest);

    Page<Booking> findAllByBookerAndStatusEquals(User user, Status status, PageRequest pageRequest);

    //Получение списка бронирований для всех вещей текущего пользователя.
    //Эндпоинт — GET /bookings/owner?state={state}.
    //Этот запрос имеет смысл для владельца хотя бы одной вещи.
    //Работа параметра state аналогична его работе в предыдущем сценарии.
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
    Page<Booking> getStateBookingByOwnerId(Long ownerId, Status status, PageRequest pageRequest);

    @Query("select b from Booking b where b.item.id = ?1 and b.start >= ?2 " +
            "order by b.start asc")
    List<Booking> getBookingsByItem(Long itemId, LocalDateTime date);

    Booking findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime time, Status status);

    Booking findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime time, Status status);


    @Query("select b from Booking b left join Item i on b.item.id = i.id " +
            "where i.owner.id = ?1 and b.status <> 'REJECTED' " +
            "order by b.start asc")
    List<Booking> getBookingsByOwner(Long ownerId);

    @Query("select b from Booking b where b.item.id = ?1 order by b.start asc")
    List<Booking> getBookingsByItemOrderByStartAsc(Long itemId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2")
    List<Booking> getBookingsByBookerIdAndItem(Long userId, Long itemId);
}
