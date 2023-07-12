package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
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

    public static final String BOOKING_ID_COLUMN = "booking_id";
    public static final String START_TIME_COLUMN = "start_time";
    public static final String END_TIME_COLUMN = "end_time";
    public static final String ITEM_ID_COLUMN = "item_id";
    public static final String BOOKER_COLUMN = "booker_id";
    public static final String STATUS_COLUMN = "status";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = BOOKING_ID_COLUMN)
    Long id;
    @Column(name = START_TIME_COLUMN)
    LocalDateTime start;
    @Column(name = END_TIME_COLUMN)
    LocalDateTime end;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = ITEM_ID_COLUMN)
    Item item;
    @ManyToOne(fetch = FetchType.EAGER)
    User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = STATUS_COLUMN)
    BookingStatus status;
    @ManyToMany
    List<User> users = new ArrayList<>();

    public Booking() {
    }

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
    }

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status, List<User> users) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
        this.users = users;
    }
}
