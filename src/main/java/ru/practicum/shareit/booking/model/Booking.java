package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "bookings", schema = "public")
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    Long id;
    @Column(name = "start_time")
    LocalDateTime start;
    //дата и время конца бронирования
    @Column(name = "end_time")
    LocalDateTime end;
    //вещь, которую пользователь бронирует
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    Item item;
    //пользователь, который осуществляет бронирование
    @ManyToOne(fetch = FetchType.EAGER)
   // @JoinColumn(name = "user_id")
    User booker;
    //статус бронирования
    @Enumerated(EnumType.STRING)
    Status status;
    @ManyToMany
    List<User> users = new ArrayList<>();

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
    }

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, Status status, List<User> users) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
        this.users = users;
    }

    public Booking() {
    }
}
